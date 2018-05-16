package cz.neumimto.rpg.events;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 17.6.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CharacterWeaponDamageEvent extends INEntityWeaponDamageEvent {


	public CharacterWeaponDamageEvent(IActiveCharacter source, IEntity target, double damage) {
		super(source, target, damage);
	}
}
