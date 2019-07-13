package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;

public interface IPersistenceHandler {
    BaseCharacterAttribute createCharacterAttribute();

    CharacterClass createCharacterClass();

    CharacterSkill createCharacterSkill();

    CharacterBase createCharacterBase();
}
