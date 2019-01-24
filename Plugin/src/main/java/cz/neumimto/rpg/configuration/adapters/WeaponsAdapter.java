package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by NeumimTo on 5.1.2019.
 */
public class WeaponsAdapter implements AbstractSerializer<Map<ItemType, Set<ConfigRPGItemType>>> {

	@Inject
	private IEffectSourceProvider provider;

	@Override
	public Map<ItemType, Set<ConfigRPGItemType>> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode)
			throws ObjectMappingException {
		Map<ItemType, Set<ConfigRPGItemType>> map = new HashMap<>();
		List<String> list = configurationNode.getList(TypeToken.of(String.class));

		Set<WeaponClass> weaponClasses = new HashSet<>();

		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();
			if (s.toLowerCase().startsWith("weaponclass:")) {
				String clazz = s.split(":")[1];
				Optional<WeaponClass> first = NtRpgPlugin.GlobalScope.itemService.getItemTypes().stream().filter(a -> a.getName().equalsIgnoreCase(clazz)).findFirst();
				if (first.isPresent()) {
						WeaponClass weaponClass = first.get();
						weaponClasses.add(weaponClass);
					}
				}
			}

		for (WeaponClass weaponClass : weaponClasses) {
			Set<RPGItemType> items = weaponClass.getItems();
			for (RPGItemType item : items) {
				if (!hasSameitemType(list, item)) {
					list.add(item.toConfigString());
				}
			}
		}

		for (String s : list) {
			String[] split = s.split(";");
			ItemType type = Sponge.getRegistry().getType(ItemType.class, split[0]).get();
			double damage = 0;
			String displayName = null;
			if (split.length > 1) {
				try {
					damage = Double.parseDouble(split[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (split.length > 2) {
				displayName = split[2];
			}
			RPGItemType byItemTypeAndName = NtRpgPlugin.GlobalScope.itemService.getByItemTypeAndName(type, displayName);
			ConfigRPGItemType configRPGItemType = new ConfigRPGItemType(byItemTypeAndName, provider, damage);
			addToCache(map, configRPGItemType);
		}

		return map;
	}

	private boolean hasSameitemType(List<String> list, RPGItemType item) {
		for (String s : list) {
			if (s.startsWith(item.getItemType().getId()) && s.endsWith(item.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	private void addToCache(Map<ItemType, Set<ConfigRPGItemType>> cache, ConfigRPGItemType type) {
		Set<ConfigRPGItemType> configRPGItemTypes = cache.computeIfAbsent(type.getRpgItemType().getItemType(), k -> new TreeSet<>());
		configRPGItemTypes.add(type);

	}

	@Override
	public void serialize(TypeToken<?> typeToken, Map<ItemType, Set<ConfigRPGItemType>> itemTypeSetMap, ConfigurationNode configurationNode) {

	}
}

