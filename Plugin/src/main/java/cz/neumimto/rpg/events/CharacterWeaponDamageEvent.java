package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by ja on 17.6.2017.
 */

public class CharacterWeaponDamageEvent extends INEntityWeaponDamageEvent {

	private IActiveCharacter character;
	public CharacterWeaponDamageEvent(IActiveCharacter source, IEntity target, double damage) {
		super(source, target, damage);
	}

	public CharacterWeaponDamageEvent(IActiveCharacter character, IEntity iEntity) {
		super(character, iEntity);
	}
}
