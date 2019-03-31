package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.RPGItemWrapper;
import org.spongepowered.api.item.ItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class CharacterWeaponUpdateEvent extends AbstractCharacterEvent {

	private final Map<ItemType, RPGItemWrapper> allowedArmor;

	public CharacterWeaponUpdateEvent(IActiveCharacter character, Map<ItemType, RPGItemWrapper> allowedArmor) {
		super(character);
		this.allowedArmor = allowedArmor;
	}

	public Map<ItemType, RPGItemWrapper> getAllowedWeapons() {
		return allowedArmor;
	}
}
