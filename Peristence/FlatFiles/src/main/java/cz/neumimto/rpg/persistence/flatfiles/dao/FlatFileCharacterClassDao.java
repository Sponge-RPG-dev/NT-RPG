package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.common.persistance.model.CharacterClass;

import javax.inject.Inject;
import javax.inject.Singleton;

@AutoService(ICharacterClassDao.class) //todo
@Singleton
public class FlatFileCharacterClassDao implements ICharacterClassDao {

    @Inject
    private IPlayerDao playerDao;

    @Override
    public void update(CharacterClass characterClass) {
        playerDao.update(characterClass.getCharacterBase());
    }
}
