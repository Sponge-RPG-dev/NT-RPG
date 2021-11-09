package cz.neumimto.rpg.model;

import cz.neumimto.rpg.common.model.BaseCharacterAttribute;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.CharacterClass;
import cz.neumimto.rpg.common.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;

public class TestPersistanceHandler implements IPersistenceHandler {
    @Override
    public BaseCharacterAttribute createCharacterAttribute() {
        return new BaseCharacterAttributeTest();
    }

    @Override
    public CharacterClass createCharacterClass() {
        return new CharacterClassTest();
    }

    @Override
    public CharacterSkill createCharacterSkill() {
        return new CharacterSkillTest();
    }

    @Override
    public CharacterBase createCharacterBase() {
        return new CharacterBaseTest();
    }
}
