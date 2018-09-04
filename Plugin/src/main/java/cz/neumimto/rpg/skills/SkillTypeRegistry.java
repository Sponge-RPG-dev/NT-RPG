package cz.neumimto.rpg.skills;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 15.7.2018.
 */
public class SkillTypeRegistry implements AdditionalCatalogRegistryModule<ISkillType> {

	@RegisterCatalog(ISkillType.class)
	private final Map<String, ISkillType> types = Maps.newHashMap();


	@Override
	public void registerAdditionalCatalog(ISkillType extraCatalog) {
		types.put(extraCatalog.getId(), extraCatalog);
	}

	@Override
	public Optional<ISkillType> getById(String id) {
		return Optional.ofNullable(types.get(id.toLowerCase()));
	}


	@Override
	public Collection<ISkillType> getAll() {
		return types.values();
	}
}
