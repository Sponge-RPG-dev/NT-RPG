package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public interface AbstractSerializer<T> extends TypeSerializer<T> {

	@Override
	default T deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	default void serialize(TypeToken<?> typeToken, T t, ConfigurationNode configurationNode) throws ObjectMappingException {
		throw new RuntimeException("Not Implemented");
	}
}
