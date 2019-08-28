package cz.neumimto.rpg.model;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;

/**
 * Created by ja on 8.10.2016.
 */
public class CharacterSkillTest extends TimestampEntityTest implements CharacterSkill {

    private Long skillId;
    private CharacterBase characterBase;
    private int level;
    private CharacterClass fromClass;
    private String catalogId;
    private Long cooldown;

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
