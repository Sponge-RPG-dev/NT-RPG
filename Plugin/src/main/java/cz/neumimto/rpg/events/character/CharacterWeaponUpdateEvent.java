package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.RPGItemWrapper;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.item.ItemType;

import java.util.Map;

/**
 * Created by NeumimTo on 10.10.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CharacterWeaponUpdateEvent extends CancellableEvent {
	private final IActiveCharacter character;
	private final Map<ItemType, RPGItemWrapper> allowedArmor;

	public CharacterWeaponUpdateEvent(IActiveCharacter character, Map<ItemType, RPGItemWrapper> allowedArmor) {
		this.character = character;
		this.allowedArmor = allowedArmor;
	}

	public IActiveCharacter getCharacter() {
		return character;
	}

	public Map<ItemType, RPGItemWrapper> getAllowedWeapons() {
		return allowedArmor;
	}
}
