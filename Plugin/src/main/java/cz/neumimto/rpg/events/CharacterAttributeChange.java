package cz.neumimto.rpg.events;

/**
 * Created by NeumimTo on 23.1.2016.
 */
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
