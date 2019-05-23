package cz.neumimto.rpg.skills.mods;

import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 14.10.2018.
 */
public class SkillPreProcessorFactoryRegistry implements AdditionalCatalogRegistryModule<SkillPreProcessorFactorySpongeWrapper> {

	@RegisterCatalog(SkillPreProcessorFactory.class)
	private Map<String, SkillPreProcessorFactorySpongeWrapper> cache = new HashMap<>();

	@Override
	public void registerAdditionalCatalog(SkillPreProcessorFactorySpongeWrapper extraCatalog) {
		cache.put(extraCatalog.getId().toLowerCase(), extraCatalog);
	}

	@Override
	public Optional<SkillPreProcessorFactorySpongeWrapper> getById(String id) {
		return Optional.ofNullable(cache.get(id.toLowerCase()));
	}

	@Override
	public Collection<SkillPreProcessorFactorySpongeWrapper> getAll() {
		return cache.values();
	}
}
