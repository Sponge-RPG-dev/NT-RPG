package cz.neumimto.players;

/**
 * Created by NeumimTo on 17.3.2015.
 */
public interface CharacterAttribute {
    String getName();

    void setName();

    void setDescription(String string);

    String getDescription();

    void recalculateCharacterStats();
}
