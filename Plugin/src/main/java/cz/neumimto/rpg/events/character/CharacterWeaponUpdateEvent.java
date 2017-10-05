package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.ItemType;

import java.util.Map;
import java.util.TreeSet;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class CharacterWeaponUpdateEvent extends CancellableEvent {
	private final IActiveCharacter character;
	private final Map<ItemType, TreeSet<ConfigRPGItemType>> allowedArmor;

	public CharacterWeaponUpdateEvent(IActiveCharacter character, Map<ItemType, TreeSet<ConfigRPGItemType>> allowedArmor) {
		this.character = character;
		this.allowedArmor = allowedArmor;
	}

	public IActiveCharacter getCharacter() {
		return character;
	}

	public Map<ItemType, TreeSet<ConfigRPGItemType>> getAllowedWeapons() {
		return allowedArmor;
	}
}
