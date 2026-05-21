package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MyLanguageProvider extends LanguageProvider {
    public MyLanguageProvider(PackOutput output) {
        super(output, LanfasieBenderson.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("entity.lanfasie_benderson.benderson.name.deep_latent", "Team Soul of Deep Latent");
    }
}
