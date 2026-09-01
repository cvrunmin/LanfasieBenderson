# Refactoring Plan — Generic Phase State Machine

*Companion to: [benderson-transitioner-analysis.md](./benderson-transitioner-analysis.md)*

## 0. Goals and constraints

Decisions confirmed for this plan:

1. **Reusable across entities and mods** — the same system will be used by other living entities in this mod and by other mods in the future (cloned codebase or an extracted independent library).
2. **Definitions in Java code** — no data-driven JSON; definitions are immutable records/constants/factories in code.
3. **Multi-instance safe** — multiple instances of the same phased entity (e.g., two `Benderson` bosses) may exist simultaneously. This forces per-entity runtime state.
4. **Behavior-preserving** — the refactor must reproduce the current logic exactly, including known quirks.

## 1. Direct answer to "Do we need to separate data from logic?"

**Yes — but at the right granularity: split *definition* from *runtime state*, and keep the logic with the runtime state.**

Today the split is accidental: each `Benderson` constructor builds its own phase objects, so two bosses never collide — but the *definition* (durations, damages, radii, animation names) is welded to the *runtime* (`currentTick`, `cooldownTick`, `trackingMarker`, `lastSeed`, …) inside the same mutable object. That entanglement costs:

- **Reuse across entities**: a future boss wanting a `KnockbackFromCenter`-style attack at different radii/damage must copy the class or add constructor params — and every new entity duplicates the machine wiring in its constructor.
- **Save-format stability**: persisted data and tuning parameters are the same object, so "retune a phase" risks breaking old saves.
- **Testability**: the FSM core is untestable in isolation because it drags `Benderson` + `ServerConfig` + arenas in.

**Proposed split:**

| Piece | Contents | Mutable? | Shared? | Persisted? |
|---|---|---|---|---|
| `PhaseStateDefinition<T>` | id, immutable params, factory `T → IPhaseState<T>` | no | yes (per entity type) | no |
| `PhaseStateInstance<T>` (today's `IPhaseState`) | logic (`start`/`tick`/`end`/`inactiveTick`/`canUse`) + runtime fields | yes | no (per entity) | yes |
| `PhaseMachineDefinition<T>` | state definitions + transition table + fallback key | no | yes (per entity type) | no |
| `PhaseMachine<T>` (today's `PhaseStateTransitioner`) | per-entity instances, `currentState`, `shouldChangePhase`, `phaseChangedCount`, listener | yes | no | yes |

**Deliberately *not* recommended**: the full flyweight pattern (logic in the definition, state passed in). Phase logic is entity-specific anyway — the reusable parts are the *framework* (FSM, scheduling, persistence scaffolding) and the *parameters*, and this split captures both without the indirection cost.

## 2. Target architecture

**Core package** — `io.github.cvrunmin.lanfasie.benderson.foundation.phase` (new; zero imports of `content.benderson`, `compat.*`, configs, or arenas — only `net.minecraft.*` + JDK, so it can be copied into other mods verbatim):

```java
// Contract for any phase
public interface IPhaseState<T extends Entity> {
    void start();
    boolean tick();                 // false == phase completed
    void end();
    default void inactiveTick() {}
    default boolean canUse() { return true; }
    default void addAdditionalSaveData(ValueOutput out) {}
    default void readAdditionalSaveData(ValueInput in) {}
    default OptionalDouble syncSecondForClient() { return OptionalDouble.empty(); }
}

// Immutable, shared per entity type
public final class PhaseStateDefinition<T extends Entity> {
    private final String id;
    private final Function<T, IPhaseState<T>> factory;   // closes over immutable params
}

public final class PhaseMachineDefinition<T extends Entity> {
    private final Map<String, PhaseStateDefinition<T>> states;
    private final Map<String, Map<String, DestRecord>> transitions;  // DestRecord(priority, weight) — as today
    private final String fallbackStateKey;
    public PhaseMachine<T> createMachine(T owner);       // instantiates one IPhaseState per definition
}

// Per-entity runtime — the old PhaseStateTransitioner, genericized, with the *identical* tick() algorithm
public final class PhaseMachine<T extends Entity> {
    public void tick();
    public boolean isShouldChangePhase();
    public void setPhaseState(String key);
    public Optional<IPhaseState<T>> getPhaseState();
    public String getPhaseStateId();
    public int getPhaseChangedCount();
    public void resetPhaseChangedCount();
    public void setOnChangePhaseListener(BiConsumer<String, IPhaseState<T>> listener);
    public void addAdditionalSaveData(ValueOutput out);
    public void readAdditionalSaveData(ValueInput in);
    public T getOwner();
}

// Fluent registration, preserving current validation semantics
public final class PhaseMachineBuilder<T extends Entity> {
    public PhaseMachineBuilder<T> addPhaseState(String key, PhaseStateDefinition<T> def); // duplicate key -> IllegalArgumentException
    public PhaseMachineBuilder<T> addTransition(String from, String to);                  // weight 1
    public PhaseMachineBuilder<T> addTransition(String from, String to, int priority);
    public PhaseMachineBuilder<T> addTransition(String from, String to, int priority, int weight); // weight<=0 -> IAE
    public PhaseMachineBuilder<T> setFallback(String key);                                 // must be registered
    public PhaseMachineDefinition<T> build();                                              // first added state becomes fallback if none set
}

// Optional, for the "define once per entity type" story
public final class PhaseMachineRegistry {
    public static <T extends Entity> void register(EntityType<?> type, Function<Entity, PhaseMachine<?>> factory);
    public static PhaseMachine<?> create(Entity entity);   // lazily built on first spawn
}
```

**Entity side** — `content.benderson` keeps: `BendersonPhaseState<T>` base (or each phase typed `<Benderson>`), the 13 phase classes (constructors changed to take params), the machine wiring, and all Benderson-specific couplings (§4). Nothing Benderson-specific leaks into `foundation.phase`.

## 3. Behavior-preservation contract (must be byte-identical)

The new `PhaseMachine.tick()` must reproduce `PhaseStateTransitioner.tick()` exactly:

1. **Branch A**: `currentState == null` → enter `fallbackStateKey`, `changed = true`.
2. **Branch B** (shouldChangePhase): filter by `state != null` **and** `state.canUse()`; priority scan from `Integer.MIN_VALUE` (higher clears list, equal appends); single candidate → take it; multiple → weighted random via `Arrays.parallelPrefix(weights, Integer::sum)` + `rand.nextInt(total)` + first bucket `randNum < prefix[i]`, with RNG from `owner.level().getRandom().fork()`, falling back to `new LegacyRandomSource(new Random().nextLong())`. **No eligible candidate → keep polling** (flag stays true, current state frozen, no `tick()` that tick).
3. **Branch C**: `state.tick()` returns false → `state.end()` + flag; unknown current key → flag.
4. **changed block**: `state.start()` (if non-null), clear flag, fire listener (if both non-null), `phaseChangedCount++` with the same overflow guard.
5. **Tail**: `inactiveTick()` for every registered state that is not current.
6. **Forced `setPhaseState`**: end current, gate on `canUse()` — start + clear flag + fire listener when usable, else set flag only; **no count increment, no listener when unusable** (asymmetric today — preserved deliberately).
7. **Persistence keys**: `Phase`, `PhaseData/<key>`, `PhaseChangedCount`, and every per-phase key (`Tick`, `Cooldown`, `Marker`, `Seed`, `NextType`, `LastSword`, …) unchanged, so existing saves load unchanged.

## 4. What stays out of the core (entity-side integration, untouched)

- `progressPhaseState()` + the mirrored `shouldChangePhase` field used by `TopEnmityTargetGoal`/`NearestTargetGoal` and the 2-tick target-selector refresh.
- `isInvulnerable()` → `machine.getPhaseState().orElse(null) instanceof KnockbackFromCenterPhaseState` (still valid; add a `machine.isInPhase(Class)` convenience if desired).
- `writeSpawnData`/`readSpawnData` + `syncSecondForClient()`.
- The change listener → `ProjectMeCompat` → `RedisSynchronizer.entityPhaseStateChanged(...)`, including its **entity-specific downcast** to `SummonAnticalabrumPhaseState` for the `NextType`/`Seed` payload (`RedisSynchronizer.java:195–201`). This is why the core must expose the *instance* (`BiConsumer<String, IPhaseState<T>>`), not just the id.
- Phase access to the machine: `IdlePhaseState.end()` and `KnockbackFromCenterPhaseState.canUse()` currently reach it via `owner.getTransitioner()`. Keep `Benderson.getTransitioner()` (returning `PhaseMachine<Benderson>`) so the two call sites compile unchanged.

## 5. Ease-of-definition deliverables

**A. The builder DSL** — Benderson's constructor block collapses to:

```java
PhaseMachineBuilder<Benderson> b = new PhaseMachineBuilder<>();
b.addPhaseState("idle", PhaseStateDefinition.of("idle", o -> new IdlePhaseState(o)))
 .addPhaseState("circle_aoe_self", PhaseStateDefinition.of("circle_aoe_self",
        o -> new CircleAoeSelfPhaseState(o, new CircleAoeParams(circleAoeAttackDamage, 10))))   // params record = the "definition"
 ... 13 states ...
b.addTransition("idle", "idle", 0).addTransition("idle", "summon_anticalabrum") ...;           // 37 edges, identical
phaseMachine = b.build().createMachine(this);
```

**B. Reusable abstract bases** in `foundation.phase` (each maps 1:1 onto patterns already in the code, so porting is mechanical):

- `AbstractTimedPhaseState<T>` — tick counter + completion at a bound (Idle, ArenaEntering, Elevate, Knockback, the AoE attacks).
- `AbstractCooldownPhaseState<T>` — `inactiveTick()` cooldown decrement + `canUse()` cooldown check (the attack states today), plus a `globalCooldown` gate hook.
- `AbstractAnimationPhaseState<T>` — the start/loop/end animate-state choreography shared by most attack phases.

**C. `PhaseMachineRegistry`** — per `EntityType<?>` factory built lazily on first spawn (important: the current constructor reads `ServerConfig` inside a try/catch with fallback values — the factory lambda must preserve that timing and fallback, so first-spawn laziness is the right moment).

**D. A worked example** — the final stage adds a tiny second consumer (or a documented skeleton) to prove a future mod/entity needs only: entity constructor builds a machine via the builder, `customServerAiStep` calls `machine.tick()`, entity mirrors the flag, registers the listener. Total new-code cost per entity ≈ 30 lines + its phase classes.

## 6. Migration stages (each ends green before the next)

| # | Stage | Deliverable / exit criterion |
|---|---|---|
| 1 | **Characterization tests** | A pure-Java FSM harness (fake owner with a `RandomSource`, scripted `canUse`/`tick` return sequences, the exact transition table from `Benderson.java:158–204`) that records golden state trajectories for ~20 scripted scenarios. This *captures* today's behavior before touching code. |
| 2 | **Core package** | `foundation.phase` with the generic `PhaseMachine` replicating the algorithm; must pass every Stage-1 golden test. Old `PhaseStateTransitioner` still in use by the game. |
| 3 | **Port the 13 phases** | Convert each to `IPhaseState<Benderson>` + params record; `start`/`tick`/`end`/`canUse`/persistence copied verbatim; animation-name constants stay. Persistence keys unchanged. |
| 4 | **Rewire Benderson** | Replace the constructor block with the builder; keep `getTransitioner()` as a delegating getter; delete `PhaseStateTransitioner.java`. Exit: full run — fight both body states end-to-end, save/load mid-fight (including mid-`elevate_to_extreme`), two concurrent bosses, Project Me projection. |
| 5 | **Second consumer proof** | Optional but recommended: a second minimal phased entity (or a test-only registration) proving the registry + base classes work outside Benderson. |
| 6 | **Extraction readiness + docs** | Verify `foundation.phase` has zero mod-specific imports; write the "Defining a new phase/entity" doc with the builder example. From here, copying the package into another mod (or a real library project) is a file-copy. |

## 7. Risks & mitigations

- **Behavior drift** — the biggest risk; mitigated by Stage 1 golden tests written *before* the rewrite, plus keeping the tick body a mechanical copy.
- **RNG parity** — `fork()` + `parallelPrefix` must be copied verbatim; the fallback `LegacyRandomSource` path preserved for the no-owner/no-level case.
- **Save compatibility** — all keys preserved (§3.7); old `Phase` values are all still registered ids, so no migration code needed.
- **Config-load timing** — registry factory must stay lazy (first spawn) to keep the current `IllegalStateException` fallback semantics.
- **Project Me** — listener signature and `RedisSynchronizer` downcast are entity-side; the core change doesn't touch the message format (`phaseId` + `CompoundTag`).
- **`getTransitioner()` public API** — only two phase classes use it internally; keep the delegating getter so those call sites don't change.

## 8. Explicitly out of scope (quirks preserved, per "behavior-preserving")

Listed as follow-up candidates, *not* part of this refactor:

- `setPhaseState` not firing the listener / not counting when the target fails `canUse()`;
- `Integer.MIN_VALUE` priority start;
- self-loop restart semantics (`attack → attack` re-`start()`s without an intermediate `end()`);
- the `phaseChangedCount` overflow guard's unreachability.

If any of these should be fixed later, they are one-line semantic changes on top of the new core.
