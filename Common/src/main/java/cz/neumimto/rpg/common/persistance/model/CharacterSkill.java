package cz.neumimto.rpg.common.persistance.model;

public class CharacterSkill extends TimestampEntity {

    private Long skillId;
    private CharacterBase characterBase;
    private int level;
    private CharacterClass fromClass;
    private String catalogId;
    private long cooldown;

    public Long getId() {
        return skillId;
    }

    public void setId(Long id) {
        this.skillId = id;
    }

    public CharacterBase getCharacterBase() {
        return characterBase;
    }

    public void setCharacterBase(CharacterBase characterBase) {
        this.characterBase = characterBase;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CharacterClass getFromClass() {
        return fromClass;
    }

    public void setFromClass(CharacterClass fromClass) {
        this.fromClass = fromClass;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public Long getCooldown() {
        return cooldown;
    }

    public void setCooldown(Long cooldown) {
        this.cooldown = cooldown;
    }
}
