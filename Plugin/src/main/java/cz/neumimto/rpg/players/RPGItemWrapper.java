package cz.neumimto.rpg.players;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;

import java.util.HashSet;
import java.util.Set;

public class RPGItemWrapper {

	private Set<ConfigRPGItemType> items = new HashSet<>();

	public static RPGItemWrapper createFromSet(Set<ConfigRPGItemType> types) {
		RPGItemWrapper wrapper = new RPGItemWrapper();
		wrapper.addItems(types);
		return wrapper;
	}

	public void addItem(ConfigRPGItemType type) {
		items.add(type);
	}

	public void removeItem(ConfigRPGItemType type) {
		if (items.contains(type)) {
			items.remove(type);
		}
	}

	public boolean containsItem(RPGItemType from) {
		for (ConfigRPGItemType item : items) {
			if (item.getRpgItemType().getItemType() == from.getItemType()) {
				if (item.getRpgItemType().getDisplayName() == null && from.getDisplayName() == null) {
					return true;
				}
				if (from.getDisplayName() != null && from.getDisplayName().equals(item.getRpgItemType().getDisplayName())) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<ConfigRPGItemType> getItems() {
		return items;
	}

	public double getDamage(RPGItemType rpgItemType) {
		double damage = 0;
		switch (pluginConfig.WEAPON_MERGE_STRATEGY) {
			case 2:

				for (ConfigRPGItemType item : items) {
					if (item.rpgItemType.equals(rpgItemType)) {
						damage = Math.max(damage, item.damage);
					}
				}
				break;
			case 1:
				for (ConfigRPGItemType item : items) {
					if (item.rpgItemType.equals(rpgItemType)) {
						damage += item.damage;
					}
				}
		}
		return damage;
	}

	public void addItems(Set<ConfigRPGItemType> value) {
		for (ConfigRPGItemType configRPGItemType : value) {
			addItem(configRPGItemType);
		}
	}
}
