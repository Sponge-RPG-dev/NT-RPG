package cz.neumimto.rpg.skills;

import org.spongepowered.api.CatalogType;

public abstract class SkillConfigLoader implements CatalogType {

    private final String id;
    private final String name;

    public abstract ISkill build(String skillname);

    public SkillConfigLoader(String id) {
        this.id = id;
        this.name = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
