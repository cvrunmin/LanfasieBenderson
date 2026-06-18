package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import net.neoforged.fml.ModList;

public class ProjectMeCompat {

    private static AbstractSynchronizer synchronizerBackend;

    static {
        if (ModList.get().isLoaded("project_me")) {
            try{
                var mainClass = Class.forName("cn.zbx1425.projectme.ProjectMe");
                var configField = mainClass.getDeclaredField("CONFIG");
                var config = configField.get(null);
                var configClass = config.getClass();
                var redisUrlField = configClass.getDeclaredField("redisUrl");
                var redisUrlConfig = redisUrlField.get(config);
                synchronizerBackend = new RedisSynchronizer(new ReflectiveConfigItemAccessor<String>(redisUrlConfig, String.class));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                synchronizerBackend = new DummySynchronizer();
            }
        }else{
            synchronizerBackend = new DummySynchronizer();
        }
    }

    public static AbstractSynchronizer getSynchronizerBackend() {
        return synchronizerBackend;
    }
}
