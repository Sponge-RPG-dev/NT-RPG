package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.WeaponClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Optional;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class WeaponClassAdapter implements AbstractSerializer<WeaponClass> {

	@Override
	public WeaponClass deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		String string = configurationNode.getString();
		Optional<WeaponClass> first =
				NtRpgPlugin.GlobalScope.itemService.getItemTypes().stream().filter(a -> a.getName().equalsIgnoreCase(string)).findFirst();
		if (!first.isPresent()) {
			throw new ObjectMappingException("Unknown weapon class \""+string+" \"");
		}
		return first.get();
	}
}
