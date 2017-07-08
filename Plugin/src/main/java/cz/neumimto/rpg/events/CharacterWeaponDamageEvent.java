package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 17.6.2017.
 */

public class CharacterWeaponDamageEvent extends INEntityWeaponDamageEvent {


	public CharacterWeaponDamageEvent(IActiveCharacter source, IEntity target, double damage) {
		super(source, target, damage);
	}
}
