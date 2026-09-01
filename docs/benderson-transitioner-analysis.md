# Analysis: `Benderson.transitioner` and `PhaseStateTransitioner`

*Package: `io.github.cvrunmin.lanfasie.benderson.content.benderson`*

## 1. The field inside `Benderson`

Declared at `Benderson.java:104` as `private PhaseStateTransitioner transitioner;` (not initialized at declaration), then constructed in the entity constructor (`Benderson.java:133`):

```java
transitioner = new PhaseStateTransitioner(this);
```

It is the boss's phase state machine. The entity wires it up in the constructor via fluent calls:

- **`addPhaseStateInstance(...)` × 13** (`Benderson.java:158–170`) — registers the states `idle, arena_entering, attack, lethal_attack, circle_aoe_self, circle_stack, three-fourth_arena_aoe, three-fourth_arena_aoe_extreme, summon_anticalabrum, elevate_to_extreme, knockback_from_center, summon_blocking_pile, ecliptic_meteor`. Each state is a concrete `IPhaseState` implementation in the `phases` sub-package, holding a reference back to the owner.
- **`addTransition(...)` × 37** (`Benderson.java:171–204`) — the transition graph (see §4).
- **`setOnChangePhaseListener(this::onPhaseStateChanged)`** (`Benderson.java:205`) — the listener (`Benderson.java:467`) forwards to `ProjectMeCompat.getSynchronizerBackend().entityPhaseStateChanged(...)`, which syncs phase changes to projected (Project Me) copies via `EntityChangePhaseStateMessage` → `ProjectedBenderson.setPhaseState(phaseId, extraData)`.

All other touchpoints of the field in `Benderson`:

| Location | Usage |
|---|---|
| `Benderson.java:418–422` `progressPhaseState()` | Server-only: calls `transitioner.tick()`, then mirrors `shouldChangePhase = transitioner.isShouldChangePhase()` into the entity field (guarded by `if(this.level().isClientSide()) return;`) |
| `Benderson.java:424–426` `setPhaseState(String)` | Public delegate for **forced** transitions (see §5) |
| `Benderson.java:571` / `:605` | `readAdditionalSaveData` / `addAdditionalSaveData` pass-through |
| `Benderson.java:911` | `getTransitioner()` getter — used by phases themselves (e.g. `IdlePhaseState.end()` resets the counter; `KnockbackFromCenterPhaseState.canUse()` reads it) |
| `Benderson.java:960` | `writeSpawnData` — `getPhaseState().map(IPhaseState::syncSecondForClient)` so the client can seek the animation timeline on spawn (`clientSyncApproxAnimTime`) |
| `Benderson.java:1061` | `isInvulnerable()` — invulnerable while in `KnockbackFromCenterPhaseState` |

The mirrored entity-level `shouldChangePhase` field is what the two target goals (`TopEnmityTargetGoal`, `NearestTargetGoal`, `Benderson.java:1011–1130`) key off: `canUse()` returns `!owner.shouldChangePhase`, and `customServerAiStep` force-refreshes `targetSelector.tick()` every 2 ticks while it's true — i.e. the boss re-acquires a target during the phase-change window, before the next phase's `start()` runs.

## 2. `PhaseStateTransitioner` structure

```java
private final Benderson owner;                                  // back-reference
private final Map<String, IPhaseState> possiblePhaseState;      // registered states
private final Map<String, Map<String, DestRecord>> possibleTransition; // from -> {to -> {priority, weight}}
private String fallbackStateKey;    // first registered state, or via setFallback()
private String currentState;        // key of the active state (null = never started)
private boolean shouldChangePhase;  // requests a transition on next tick
private int phaseChangedCount;      // scheduler-driven changes since last reset
private BiConsumer<String, IPhaseState> onChangePhaseListener;
// private record DestRecord(int priority, int weight) {}
```

**State contract** (`IPhaseState`, 8 methods, all defaulted except 3):
- `start()` / `tick()` / `end()` — lifecycle
- `tick()` returning **`false` means "this phase is done"** — that is the sole completion signal
- `canUse()` — eligibility gate checked **before entering** a state during a transition (default `true`)
- `inactiveTick()` — per-tick callback for **non-current** states (used for cooldown countdowns: `cooldownTick--` in `KnockbackFromCenterPhaseState`, `LethalAttackPhaseState`, `SummonAnticalabrumPhaseState`)
- `addAdditionalSaveData` / `readAdditionalSaveData` — per-phase persistence
- `syncSecondForClient()` — spawn-time animation sync (overridden by `ArenaEnteringPhaseState`, `ElevateToExtremeState`)

## 3. The `tick()` state machine

Called once per server tick from `progressPhaseState()`. Three mutually exclusive branches:

**Branch A — first run** (`currentState == null`): enters `fallbackStateKey` (the first registered state, `idle`), marks `changed`.

**Branch B — transition requested** (`shouldChangePhase == true`): evaluates the outgoing transition table `possibleTransition.get(currentState)`:
1. **Filter**: skip destinations that aren't registered or whose `state.canUse()` is false.
2. **Priority selection**: track `curPriority` (init `Integer.MIN_VALUE`). A candidate with lower priority is skipped; higher priority clears the candidate list; equal priority is appended. So it always selects the **highest-priority eligible group**.
3. **Weighted pick**: 1 candidate → take it; ≥2 candidates → weighted random via prefix sums (`Arrays.parallelPrefix(weightArray, Integer::sum)`), then `rand.nextInt(totalWeight)` and first bucket where `randNum < prefix[i]`. RNG comes from `owner.level().getRandom().fork()` (a forked `RandomSource`), falling back to a `LegacyRandomSource` if no owner/level.
4. No eligible candidates → nothing happens; `shouldChangePhase` stays `true` and the poll repeats next tick (this is how the machine *waits* for a condition, e.g. for a target to appear so `attack.canUse()` becomes true).

**Branch C — steady state**: ticks the current state. If `state.tick()` returns `false` → `state.end()` + `shouldChangePhase = true` (transition happens *next* tick). If the current key isn't registered → immediately set `shouldChangePhase = true`.

**On `changed`**: `state.start()`, `shouldChangePhase = false`, fire `onChangePhaseListener.accept(currentState, state)`, and `phaseChangedCount++` (with an overflow guard).

**Finally**: `inactiveTick()` for every registered state that is not current.

## 4. The transition graph (as configured)

```
idle ──(0)→ idle | (1)→ summon_anticalabrum, attack
arena_entering ──(0)→ attack | (-1)→ idle
attack ──(-1)→ idle | (0)→ attack | (10)→ summon_anticalabrum, knockback_from_center
       | (1)→ lethal_attack, circle_aoe_self, circle_stack,
              three-fourth_arena_aoe, three-fourth_arena_aoe_extreme
lethal_attack / circle_aoe_self / circle_stack / three-fourth_arena_aoe
 / three-fourth_arena_aoe_extreme / summon_anticalabrum ──(0)→ idle | (1)→ attack
elevate_to_extreme ──(1)→ idle
knockback_from_center ──(10)→ summon_blocking_pile | (-1)→ idle
summon_blocking_pile ──(10)→ ecliptic_meteor | (-1)→ idle
ecliptic_meteor ──(1)→ attack | (0)→ idle
```

Priority is a hard tier, weight a soft tiebreak:
- **10** = scripted combo chain (`summon_anticalabrum` / `knockback_from_center` from `attack`; the extreme chain `knockback_from_center → summon_blocking_pile → ecliptic_meteor` is a 10-vs-(-1) cascade that always wins when usable).
- **1** = the ordinary "pick an attack" pool (all weighted 1:1:1:1:1 among the five attack states, gated by `canUse()`).
- **0** = self-loop (`attack → attack`, `idle → idle`) and safe returns.
- **-1** = escape hatch back to `idle`, only taken when nothing better is eligible.

The combat loop is thus: from `attack`, every exit tries the priority-10 specials first (gated by `canUse()`: `SummonAnticalabrum` needs `DEEP_LATENT`, a target, no cooldown, and no live summoned sword; `KnockbackFromCenter` needs either `UNVEILED` at ≤50% HP or `UNFORGIVEN` with `phaseChangedCount > 15`, both off cooldown), else a weighted pick from the five attacks, else self-loop at 0; `idle` is a last resort at -1. `idle` also resets `phaseChangedCount` on `end()`, so that counter is a "time since last idle" proxy.

## 5. Forced transitions (`setPhaseState`)

`Benderson.setPhaseState(String)` → `transitioner.setPhaseState(key)` is the **interrupt** path, used by:
- `OminousOrbItem.java:63` → `"arena_entering"` (fight start),
- `Benderson.hurtServer` (`Benderson.java:686`) → `"elevate_to_extreme"` (a would-be killing blow at >90% HP while `DEEP_LATENT` triggers the "Unforgiven" phase — the code then adds extreme attribute modifiers and caps the damage to 0.01).

Semantics differ from scheduler transitions: the current state's `end()` is called unconditionally, then if the new state `canUse()` it is `start()`-ed (listener fired, flag cleared); otherwise it is entered in a "pending exit" state with `shouldChangePhase = true` so the next tick immediately transitions out. Notably, **forced transitions do not increment `phaseChangedCount` and never go through priority/weight selection**.

## 6. Persistence

`addAdditionalSaveData` writes `Phase` (current key), a `PhaseData` child per registered state (delegated per-phase), and `PhaseChangedCount`. `readAdditionalSaveData` restores the current key **only if it is still registered** (otherwise the next tick's null/unknown-key handling falls back), restores per-phase data, and the counter. The client never ticks the machine (`progressPhaseState` returns early on the client); it receives phase info indirectly through the synced `ANIMATE_STATE`/`BODY_STATE` entity data and the spawn-time `syncSecondForClient()` timeline seek.

## 7. Notable semantics / observations

1. **Completion is pull-based**: phases self-terminate by returning `false` from `tick()`; the transitioner never commands a phase to stop. A phase's `end()` is called exactly once, by the completion path (or by a forced `setPhaseState`).
2. **Self-loops restart phases**: `attack → attack` re-invokes `start()` (after the old instance's `end()`), giving a clean per-attack reset.
3. **Frozen completed states**: while waiting for an eligible candidate, the completed state is neither ticked nor `inactiveTick`-ed (it's still "current"), so nothing advances until eligibility appears — that's the intended gating mechanism (e.g. waiting for a target).
4. **The extreme ("Unforgiven") mode** is a state machine of its own riding on the same transitioner: `hurtServer` force-enters `elevate_to_extreme` → `ElevateToExtremeState` drives `BodyState` `DEEP_LATENT → TRANSITION_UNFORGIVEN → TRANSITION_UNFORGIVEN_POST → UNFORGIVEN` over 140 ticks → `end()` sets full HP → `canUse()` of the normal attacks then only allows the phase-count-gated `knockback_from_center` chain (meteor combo) as the priority-10 path.
5. **Minor asymmetries worth knowing** (not bugs, but easy to trip on): `setPhaseState` doesn't fire the listener nor bump the counter when the target state fails `canUse()`; `tick()` re-fires `start()`/listener when a transition picks the *same* state only through an explicit self-loop edge; and `getPhaseState()` returns `Optional.empty()` when `currentState` is null or unregistered (the code at `Benderson.java:1061` relies on that). Also, if a phase were ever added without any outgoing transition, the machine would spin in Branch B forever — but every registered state here has at least one edge, and `fallbackStateKey` is always the first-registered `idle`.

**Bottom line**: `transitioner` is a small, self-contained priority-then-weight, cooperative FSM — phases decide when they're done, `canUse()` decides whether a destination is legal, priorities hard-tier the behavior (scripted combo > normal attack pool > self-loop > idle), weights randomize within a tier, and the whole thing is driven once per server tick from `customServerAiStep`, with forced entry points for scripted events and full save/load support.
