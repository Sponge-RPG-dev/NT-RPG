package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.google.auto.service.AutoService;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.persistence.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.persistence.model.CharacterBaseImpl;
import cz.neumimto.rpg.persistence.model.CharacterClassImpl;
import cz.neumimto.rpg.persistence.model.CharacterSkillImpl;

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
