package cz.neumimto.rpg.common.model;

public interface CharacterSkill extends TimestampEntity {
    Long getId();

    void setId(Long id);

    CharacterBase getCharacterBase();

    void setCharacterBase(CharacterBase characterBase);

    int getLevel();

    void setLevel(int level);

    CharacterClass getFromClass();

    void setFromClass(CharacterClass fromClass);

    String getCatalogId();

    void setCatalogId(String catalogId);

    Long getCooldown();

    void setCooldown(Long cooldown);
}
