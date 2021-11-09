package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.CharacterClass;
import cz.neumimto.rpg.common.model.CharacterSkill;

public class CharacterSkillImpl extends TimestampEntityImpl implements CharacterSkill {

    private Long skillId;
    private CharacterBase characterBase;
    private int level;
    private CharacterClass fromClass;
    private String catalogId;
    private long cooldown;

    @Override
    public Long getId() {
        return skillId;
    }

    @Override
    public void setId(Long id) {
        this.skillId = id;
    }

    @Override
    public CharacterBase getCharacterBase() {
        return characterBase;
    }

    @Override
    public void setCharacterBase(CharacterBase characterBase) {
        this.characterBase = characterBase;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public CharacterClass getFromClass() {
        return fromClass;
    }

    @Override
    public void setFromClass(CharacterClass fromClass) {
        this.fromClass = fromClass;
    }

    @Override
    public String getCatalogId() {
        return catalogId;
    }

    @Override
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    @Override
    public Long getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(Long cooldown) {
        this.cooldown = cooldown;
    }
}
