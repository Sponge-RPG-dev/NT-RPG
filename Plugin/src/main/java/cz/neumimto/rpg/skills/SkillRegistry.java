package cz.neumimto.rpg.skills;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 1.7.2018.
 */
public class SkillRegistry implements AdditionalCatalogRegistryModule<ISkill> {

    @RegisterCatalog(ISkill.class)
    private final Map<String, ISkill> skills = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(ISkill skill) {
        skills.put(skill.getId().toLowerCase(), skill);
    }

    @Override
    public Optional<ISkill> getById(String id) {
        return Optional.ofNullable(skills.get(id.toLowerCase()));
    }

    @Override
    public Collection<ISkill> getAll() {
        return skills.values();
    }
}

