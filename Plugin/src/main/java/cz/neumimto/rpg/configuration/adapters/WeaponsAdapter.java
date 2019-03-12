package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.config.blackjack.and.hookers.annotations.EnableSetterInjection;
import cz.neumimto.config.blackjack.and.hookers.annotations.Setter;
import cz.neumimto.rpg.Log;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by NeumimTo on 5.1.2019.
 */
@EnableSetterInjection
public class WeaponsAdapter implements TypeSerializer<Map<ItemType, Set<ConfigRPGItemType>>> {


	private IEffectSourceProvider provider;

	@Setter
	public void setProvider(IEffectSourceProvider provider) {
		this.provider = provider;
	}

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
				Optional<WeaponClass> first = NtRpgPlugin.GlobalScope.itemService.getWeaponCLasses().stream().filter(a -> a.getName().equalsIgnoreCase(clazz)).findFirst();
				if (first.isPresent()) {
						WeaponClass weaponClass = first.get();
						weaponClasses.add(weaponClass);
						iterator.remove();
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
			Optional<ItemType> t = Sponge.getRegistry().getType(ItemType.class, split[0]);
			if (!t.isPresent()) {
				Log.warn("Unknown item type " + split[0]);
				continue;
			}
			ItemType type = t.get();
			double damage = 0;
			String displayName = null;
			if (split.length > 1) {
				try {
					damage = Double.parseDouble(split[1]);
				} catch (NumberFormatException e) {
					displayName = split[1];
				}
			}
			if (split.length > 2) {
				try {
					damage = Double.parseDouble(split[2]);
				} catch (NumberFormatException e) {
					displayName = split[2];
				}
			}
			RPGItemType byItemTypeAndName = NtRpgPlugin.GlobalScope.itemService.getByItemTypeAndName(type, displayName);
			if (byItemTypeAndName == null) {
				Log.warn("Unknown item defined - " + type + " " + displayName + ". Check your ItemGroups.conf");
				continue;
			}
			ConfigRPGItemType configRPGItemType = new ConfigRPGItemType(byItemTypeAndName, provider, damage);
			addToCache(map, configRPGItemType);
		}

		return map;
	}

	private boolean hasSameitemType(List<String> list, RPGItemType item) {
		for (String s : list) {
			if (s.startsWith(item.getItemType().getId())) {
				String[] split = s.split(";");
				String displayName = null;

				try {
					if (split.length > 1) {
						Double.parseDouble(split[1]);
					}
				} catch (NumberFormatException e) {
					displayName = split[1];
				}
				if (displayName == null && split.length > 2) {
					try {
						Double.parseDouble(split[2]);
					} catch (NumberFormatException e) {
						displayName = split[2];
					}
				}

				if (item.getDisplayName() != null && s.contains(item.getDisplayName())) {
					return true;
				}
				if (item.getDisplayName() == null && displayName == null) {
					return true;
				}
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

