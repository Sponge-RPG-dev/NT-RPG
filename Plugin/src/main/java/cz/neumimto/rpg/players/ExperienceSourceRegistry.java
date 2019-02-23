package cz.neumimto.rpg.players;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ExperienceSourceRegistry implements AdditionalCatalogRegistryModule<ExperienceSource> {

	@RegisterCatalog(ExperienceSource.class)
	private final Map<String, ExperienceSource> sources = Maps.newHashMap();

	@Override
	public void registerAdditionalCatalog(ExperienceSource extraCatalog) {
		sources.put(extraCatalog.getId().toLowerCase(), extraCatalog);
	}

	@Override
	public Optional<ExperienceSource> getById(String id) {
		return Optional.ofNullable(sources.get(id.toLowerCase()));
	}

	@Override
	public Collection<ExperienceSource> getAll() {
		return sources.values();
	}
}

