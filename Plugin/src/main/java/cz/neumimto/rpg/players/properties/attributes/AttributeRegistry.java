package cz.neumimto.rpg.players.properties.attributes;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 10.2.2018.
 */
public class AttributeRegistry implements AdditionalCatalogRegistryModule<ICharacterAttribute> {

	@RegisterCatalog(ICharacterAttribute.class)
	private final Map<String, ICharacterAttribute> attributes = Maps.newHashMap();


	@Override
	public void registerAdditionalCatalog(ICharacterAttribute extraCatalog) {
		checkArgument(!attributes.containsKey(extraCatalog.getId()));
		attributes.put(extraCatalog.getId(), extraCatalog);
	}

	@Override
	public Optional<ICharacterAttribute> getById(String id) {
		return Optional.of(attributes.get(id.toLowerCase()));
	}

	@Override
	public Collection<ICharacterAttribute> getAll() {
		return attributes.values();
	}
}
