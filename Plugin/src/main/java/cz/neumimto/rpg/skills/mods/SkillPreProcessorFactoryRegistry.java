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
public class SkillPreProcessorFactoryRegistry implements AdditionalCatalogRegistryModule<SkillPreProcessorFactory> {

	@RegisterCatalog(SkillPreProcessorFactory.class)
	private Map<String, SkillPreProcessorFactory> cache = new HashMap<>();

	@Override
	public void registerAdditionalCatalog(SkillPreProcessorFactory extraCatalog) {
		cache.put(extraCatalog.getId().toLowerCase(), extraCatalog);
	}

	@Override
	public Optional<SkillPreProcessorFactory> getById(String id) {
		return Optional.ofNullable(cache.get(id.toLowerCase()));
	}

	@Override
	public Collection<SkillPreProcessorFactory> getAll() {
		return cache.values();
	}
}
