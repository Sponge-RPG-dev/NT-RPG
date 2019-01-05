package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class PropertyMapAdapter implements TypeSerializer<Map<Integer, Float>> {

	@Override
	public Map<Integer, Float> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		Map<Integer, Float> map = new HashMap<>();
		//todo
		return map;
	}

	@Override
	public void serialize(TypeToken<?> typeToken, Map<Integer, Float> integerFloatMap, ConfigurationNode configurationNode)
			throws ObjectMappingException {
		throw new RuntimeException("Not Implemented");
	}
}
