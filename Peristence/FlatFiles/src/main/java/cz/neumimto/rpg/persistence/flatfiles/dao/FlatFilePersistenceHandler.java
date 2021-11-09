package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.model.BaseCharacterAttribute;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.CharacterClass;
import cz.neumimto.rpg.common.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterBaseImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterClassImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterSkillImpl;

@AutoService(IPersistenceHandler.class)
@Singleton
public class FlatFilePersistenceHandler implements IPersistenceHandler {

    @Override
    public BaseCharacterAttribute createCharacterAttribute() {
        return new BaseCharacterAttributeImpl();
    }

    @Override
    public CharacterClass createCharacterClass() {
        return new CharacterClassImpl();
    }

    @Override
    public CharacterSkill createCharacterSkill() {
        return new CharacterSkillImpl();
    }

    @Override
    public CharacterBase createCharacterBase() {
        return new CharacterBaseImpl();
    }
}
