package cz.neumimto.rpg.api.persistance.model;

public interface BaseCharacterAttribute extends TimestampEntity {
    Long getId();

    void setId(Long id);

    CharacterBase getCharacterBase();

    void setCharacterBase(CharacterBase characterBase);

    String getName();

    void setName(String name);

    int getLevel();

    void setLevel(int level);
}
