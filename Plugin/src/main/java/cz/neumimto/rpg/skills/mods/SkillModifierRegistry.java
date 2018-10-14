package cz.neumimto.rpg.skills.mods;

import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillModifierRegistry implements AdditionalCatalogRegistryModule<SkillModifierProcessor> {

	private Map<String, SkillModifierProcessor> cache = new HashMap<>();

	@Override
	public void registerAdditionalCatalog(SkillModifierProcessor extraCatalog) {
		cache.put(extraCatalog.getId().toLowerCase(), extraCatalog);
	}

	@Override
	public Optional<SkillModifierProcessor> getById(String id) {
		return Optional.ofNullable(cache.get(id.toLowerCase()));
	}

	@Override
	public Collection<SkillModifierProcessor> getAll() {
		return cache.values();
	}
}
