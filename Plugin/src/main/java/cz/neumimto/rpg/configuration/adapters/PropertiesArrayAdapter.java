package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Map;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class PropertiesArrayAdapter implements TypeSerializer<float[]> {

	@Override
	public float[] deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		float[] arr = new float[PropertyServiceImpl.LAST_ID];

		Map<Object, ? extends ConfigurationNode> childrenMap = configurationNode.getChildrenMap();
		for (Map.Entry<Object, ? extends ConfigurationNode> objectEntry : childrenMap.entrySet()) {
			String propertyName = ((String) objectEntry.getKey()).toLowerCase();
			float f = ((Number) objectEntry.getValue().getValue()).floatValue();
			if (NtRpgPlugin.GlobalScope.spongePropertyService.exists(propertyName)) {
				int idByName = NtRpgPlugin.GlobalScope.spongePropertyService.getIdByName(propertyName);
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
