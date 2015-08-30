package cz.neumimto.players;

/**
 * Created by NeumimTo on 17.3.2015.
 */
public interface CharacterAttributeBuilder {
    CharacterAttribute build(IActiveCharacter c, String columnName);
}
