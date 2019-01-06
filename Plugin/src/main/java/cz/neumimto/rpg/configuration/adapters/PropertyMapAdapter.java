package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class PropertyMapAdapter implements AbstractSerializer<Map<Integer, Float>> {

	@Override
	public Map<Integer, Float> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		Map<Integer, Float> map = new HashMap<>();
		//todo
		return map;
	}

}
