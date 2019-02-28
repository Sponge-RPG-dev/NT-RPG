package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.properties.PropertyService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Map;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class PropertiesArrayAdapter implements AbstractSerializer<float[]> {

	@Override
	public float[] deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		float[] arr = new float[PropertyService.LAST_ID];

		Map<Object, ? extends ConfigurationNode> childrenMap = configurationNode.getChildrenMap();
		for (Map.Entry<Object, ? extends ConfigurationNode> objectEntry : childrenMap.entrySet()) {
			String propertyName = ((String) objectEntry.getKey()).toLowerCase();
			float f = ((Number) objectEntry.getValue().getValue()).floatValue();
			if (NtRpgPlugin.GlobalScope.propertyService.exists(propertyName)) {
				int idByName = NtRpgPlugin.GlobalScope.propertyService.getIdByName(propertyName);
				arr[idByName] = f;
			} else {
				throw new ObjectMappingException("Unknown property " + propertyName);
			}
		}

		return arr;
	}

	@Override
	public void serialize(TypeToken<?> typeToken, float[] floats, ConfigurationNode configurationNode) throws ObjectMappingException {

	}
}
