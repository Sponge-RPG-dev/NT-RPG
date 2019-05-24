package cz.neumimto.rpg.events.character;

/**
 * Created by NeumimTo on 23.1.2016.
 */
public class CharacterAttributeChange extends AbstractCharacterCancellableEvent {

    private final int attributeChange;

    public CharacterAttributeChange(cz.neumimto.rpg.players.IActiveCharacter character, int attributeChange) {
        super(character);
        this.attributeChange = attributeChange;
    }

    public int getAttributeChange() {
        return attributeChange;
    }
}
