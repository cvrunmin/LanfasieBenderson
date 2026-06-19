package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

public class ProjectMeCompat {

    private static AbstractSynchronizer synchronizerBackend;

    static {
        var maybeUrl = System.getenv("DEBUG_LANFASIE_BENDERSON_REDIS_URL");
        if (!FMLEnvironment.isProduction() && maybeUrl != null) {
            synchronizerBackend = new RedisSynchronizer(() -> maybeUrl);
            LanfasieBenderson.LOGGER.info("Created Redis Synchronizer using env");
        }
        else if (ModList.get().isLoaded("project_me")) {
            try{
                var mainClass = Class.forName("cn.zbx1425.projectme.ProjectMe");
                var configField = mainClass.getDeclaredField("CONFIG");
                var config = configField.get(null);
                var configClass = config.getClass();
                var redisUrlField = configClass.getDeclaredField("redisUrl");
                var redisUrlConfig = redisUrlField.get(config);
                synchronizerBackend = new RedisSynchronizer(new ReflectiveConfigItemAccessor<>(redisUrlConfig, String.class));
                LanfasieBenderson.LOGGER.info("Created Redis Synchronizer using Project Me's config");
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                LanfasieBenderson.LOGGER.warn("Project Me is loaded, but cannot find its main class, or cannot access to its redis url config", e);
                synchronizerBackend = new DummySynchronizer();
            }
        }else{
            synchronizerBackend = new DummySynchronizer();
        }
    }

    public static void activate(){
        // empty stub just to ensure that this class is loaded.
    }

    public static AbstractSynchronizer getSynchronizerBackend() {
        return synchronizerBackend;
    }
}
