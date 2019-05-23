package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import org.spongepowered.api.CatalogType;

public class SkillPreProcessorFactorySpongeWrapper extends SkillPreProcessorFactory implements CatalogType {

    private final SkillPreProcessorFactory factory;

    public SkillPreProcessorFactorySpongeWrapper(SkillPreProcessorFactory factory) {
        super(factory.getName(), factory.allowedTargets());
        this.factory = factory;
    }

    @Override
    public String getId() {
        return factory.getId();
    }

    @Override
    public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
        return factory.parse(configObject);
    }

    @Override
    public String getName() {
        return factory.getName();
    }
}
