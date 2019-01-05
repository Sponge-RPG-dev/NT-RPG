package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.inventory.RPGItemType;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class AllowedArmorListAdapter implements TypeSerializer<Set<RPGItemType>> {

	@Override
	public Set<RPGItemType> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		Set<RPGItemType> set = new HashSet<>();
		//todo
		return set;
	}

	@Override
	public void serialize(TypeToken<?> typeToken, Set<RPGItemType> rpgItemTypes, ConfigurationNode configurationNode)
			throws ObjectMappingException {
		throw new RuntimeException("Not Implemented");
	}
}
