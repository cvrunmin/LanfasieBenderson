package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.IPhaseState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class PhaseStateTransitioner {
    private final Benderson owner;
    private final Map<String, IPhaseState> possiblePhaseState = new HashMap<>();
    private final Map<String, Map<String, DestRecord>> possibleTransition = new HashMap<>();
    private String fallbackStateKey;
    private String currentState;
    private boolean shouldChangePhase;

    public PhaseStateTransitioner(Benderson owner){
        this.owner = owner;
    }

    public void tick(){
        var changed = false;
        if(currentState == null){
            currentState = fallbackStateKey;
            changed = true;
        }else if(shouldChangePhase){
            var transitionMap = possibleTransition.get(currentState);
            if(transitionMap != null){
                int curPriority = Integer.MIN_VALUE;
                List<String> candidateList = new ArrayList<>();
                var candidateWeight = new ArrayList<Integer>();
                for (Map.Entry<String, DestRecord> entry : transitionMap.entrySet()) {
                    var state = Optional.ofNullable(possiblePhaseState.get(entry.getKey())).orElse(null);
                    if(state == null) continue;
                    if(!state.canUse()) continue;
                    if(entry.getValue().priority < curPriority) continue;
                    if(entry.getValue().priority > curPriority){
                        curPriority = entry.getValue().priority;
                        candidateList.clear();
                        candidateWeight.clear();
                    }
                    candidateList.add(entry.getKey());
                    candidateWeight.add(entry.getValue().weight);
                }
                if(candidateList.size() == 1){
                    currentState = candidateList.getFirst();
                    changed = true;
                }else if(candidateList.size() > 1){
                    var rand = Optional.ofNullable(this.getOwner()).map(Entity::level).map(Level::getRandom).map(RandomSource::fork).orElse(new LegacyRandomSource(new Random().nextLong()));
                    Integer[] weightArray = candidateWeight.toArray(new Integer[0]);
                    Arrays.parallelPrefix(weightArray, Integer::sum);
                    var randNum = rand.nextInt(weightArray[weightArray.length - 1]);
                    for (int i = 0; i < weightArray.length; i++) {
                        if(randNum < weightArray[i]){
                            currentState = candidateList.get(i);
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }else{
            var state = possiblePhaseState.get(currentState);
            if(state == null) shouldChangePhase = true;
            else{
                var tickResult = state.tick();
                if(!tickResult){
                    state.end();
                    shouldChangePhase = true;
                }
            }
        }
        if(changed){
            var state = possiblePhaseState.get(currentState);
            if(state != null) state.start();
            shouldChangePhase = false;
        }
        for (Map.Entry<String, IPhaseState> entry : possiblePhaseState.entrySet()) {
            if(!Objects.equals(entry.getKey(), currentState)){
                var state = entry.getValue();
                if(state != null) state.inactiveTick();
            }
        }
    }

    public boolean isShouldChangePhase() {
        return shouldChangePhase;
    }

    public PhaseStateTransitioner setFallback(String key){
        if(key == null) return this;
        if(!possiblePhaseState.containsKey(key)) return this;
        fallbackStateKey = key;
        return this;
    }

    public void setPhaseState(String key){
        if(!possiblePhaseState.containsKey(key)) return;
        if(currentState != null){
            var state = possiblePhaseState.get(currentState);
            if(state != null) state.end();
        }
        this.currentState = key;
        var state = possiblePhaseState.get(currentState);
        if(state != null) {
            if(state.canUse()) {
                state.start();
                shouldChangePhase = false;
            }else{
                shouldChangePhase = true;
            }
        }
    }

    public String getPhaseStateId(){
        return currentState;
    }

    public IPhaseState getPhaseState(){
        if(!possiblePhaseState.containsKey(currentState)) return null;
        return possiblePhaseState.get(currentState);
    }

    public PhaseStateTransitioner addPhaseStateInstance(String key, IPhaseState phaseState){
        if(possiblePhaseState.containsKey(key)) throw new IllegalArgumentException("PhaseState key %s already exists".formatted(key));
        possiblePhaseState.put(key, phaseState);
        if(fallbackStateKey == null) fallbackStateKey = key;
        return this;
    }

    public PhaseStateTransitioner addTransition(String from, String to) {
        return addTransition(from, to, 1);
    }

    public PhaseStateTransitioner addTransition(String from, String to, int priority) {
        return addTransition(from, to, priority, 1);
    }

    public PhaseStateTransitioner addTransition(String from, String to, int priority, int weight){
        if(weight <= 0) throw new IllegalArgumentException("weight (%d) must be larger than zero".formatted(weight));
        possibleTransition.putIfAbsent(from, new HashMap<>());
        possibleTransition.get(from).put(to, new DestRecord(priority, weight));
        return this;
    }

    @Nullable
    public Benderson getOwner() {
        return owner;
    }

    public void addAdditionalSaveData(ValueOutput output){
        if(currentState != null) {
            output.putString("Phase", currentState);
        }
        var phasesObj = output.child("PhaseData");
        for (Map.Entry<String, IPhaseState> entry : this.possiblePhaseState.entrySet()) {
            var state = entry.getValue();
            if(state != null) state.addAdditionalSaveData(phasesObj.child(entry.getKey()));
        }
    }

    public void readAdditionalSaveData(ValueInput input){
        input.getString("Phase").filter(this.possiblePhaseState::containsKey).ifPresent(v -> this.currentState = v);
        var maybePhaseData = input.childOrEmpty("PhaseData");
        for (Map.Entry<String, IPhaseState> entry : this.possiblePhaseState.entrySet()) {
            var state = entry.getValue();
            if(state != null) state.readAdditionalSaveData(maybePhaseData.childOrEmpty(entry.getKey()));
        }
    }

    private record DestRecord(int priority, int weight){}
}
