package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.rpg.common.model.BaseCharacterAttribute;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.CharacterClass;
import cz.neumimto.rpg.common.model.CharacterSkill;

public interface IPersistenceHandler {
    BaseCharacterAttribute createCharacterAttribute();

    CharacterClass createCharacterClass();

    CharacterSkill createCharacterSkill();

    CharacterBase createCharacterBase();
}
