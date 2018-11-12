package cz.neumimto.rpg.events;

import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 23.1.2016.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CharacterAttributeChange extends CharacterEvent {

	private final int attributechange;

	public CharacterAttributeChange(cz.neumimto.rpg.players.IActiveCharacter character, int attributechange) {
		super(character);
		this.attributechange = attributechange;
	}


	public int getAttributechange() {
		return attributechange;
	}
}
