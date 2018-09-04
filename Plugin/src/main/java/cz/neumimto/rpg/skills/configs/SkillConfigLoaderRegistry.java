package cz.neumimto.rpg.skills.configs;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class SkillConfigLoaderRegistry implements AdditionalCatalogRegistryModule<SkillConfigLoader> {

	@RegisterCatalog(SkillConfigLoader.class)
	private final Map<String, SkillConfigLoader> types = Maps.newHashMap();


	@Override
	public void registerAdditionalCatalog(SkillConfigLoader extraCatalog) {
		types.put(extraCatalog.getId(), extraCatalog);
	}

	@Override
	public Optional<SkillConfigLoader> getById(String id) {
		return Optional.ofNullable(types.get(id.toLowerCase()));
	}


	@Override
	public Collection<SkillConfigLoader> getAll() {
		return types.values();
	}
}
