package io.github.cvrunmin.lanfasie.benderson.compat.iris;

import net.neoforged.fml.loading.FMLLoader;

public class IrisCompatEntry {
    public static boolean hasIris(){
        return FMLLoader.getCurrent().getLoadingModList().getModFileById("iris") != null;
    }

    public static void tryCompat(){
        if(hasIris()){
            IrisCompat.compat();
        }
    }
}
