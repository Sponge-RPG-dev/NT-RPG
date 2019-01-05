package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class WeaponsAdapter implements TypeSerializer<Map<ItemType, Set<ConfigRPGItemType>>> {


	@Override
	public Map<ItemType, Set<ConfigRPGItemType>> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode)
			throws ObjectMappingException {
		Map<ItemType, Set<ConfigRPGItemType>> map = new HashMap<>();
		return map;
	}

	@Override
	public void serialize(TypeToken<?> typeToken, Map<ItemType, Set<ConfigRPGItemType>> itemTypeSetMap, ConfigurationNode
			configurationNode)
			throws ObjectMappingException {
		throw new RuntimeException("Not Implemented");
	}
}

