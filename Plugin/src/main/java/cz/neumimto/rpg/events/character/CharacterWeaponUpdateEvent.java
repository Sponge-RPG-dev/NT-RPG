package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.ItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
public class CharacterWeaponUpdateEvent extends CancellableEvent {
	private final IActiveCharacter character;
	private final Map<ItemType, Double> allowedArmor;

	public CharacterWeaponUpdateEvent(IActiveCharacter character, Map<ItemType, Double> allowedArmor) {
		this.character = character;
		this.allowedArmor = allowedArmor;
	}

	public IActiveCharacter getCharacter() {
		return character;
	}

	public Map<ItemType, Double> getAllowedArmor() {
		return allowedArmor;
	}
}
