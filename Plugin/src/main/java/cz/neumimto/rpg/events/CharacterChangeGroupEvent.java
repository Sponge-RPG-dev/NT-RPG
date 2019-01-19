package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 29.4.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CharacterChangeGroupEvent extends CharacterEvent {

	private final ClassDefinition _new;
	private final ClassDefinition old;

	public CharacterChangeGroupEvent(IActiveCharacter character, ClassDefinition _new, ClassDefinition old) {
		super(character);
		this._new = _new;
		this.old = old;
	}

	public ClassDefinition getNew() {
		return _new;
	}

	public ClassDefinition getOld() {
		return old;
	}
}
