package io.github.cvrunmin.lanfasie.benderson.foundation;

import net.minecraft.util.Mth;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;

public interface IHasEnmity {
    float getMaxEnmity();

    OptionalDouble getEnmityOf(UUID uuid);

    Map<UUID, Float> getEnmityMap();

    void addEnmity(UUID uuid, float amount);

    void setEnmity(UUID uuid, float enmity);

    default EnmityBarInfo getEnmityBarInfo(UUID player){
        var enmityList1 = this.getEnmityMap();
        if(!enmityList1.containsKey(player)) return new EnmityBarInfo(-1, 1);
        var sortedPlayerList = enmityList1.entrySet().stream().sorted(Map.Entry.<UUID, Float>comparingByValue().reversed()).map(Map.Entry::getKey).toList();
        var i = sortedPlayerList.indexOf(player);
        if(i == -1) return new EnmityBarInfo(-1, 1);
        var enmity = enmityList1.get(player);
        var maxEnmity = enmityList1.get(sortedPlayerList.getFirst());
        return new EnmityBarInfo(i + 1, Mth.clamp(enmity / Math.max(0.0001f, maxEnmity), 0, 1));
    }


    record EnmityBarInfo(int rank, float barPercentage){}
}
