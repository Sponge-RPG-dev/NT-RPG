package cz.neumimto.rpg.persistance.dao;

import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 24.2.2019.
 */
@Singleton
public class JPACharacterClassDao extends GenericDao<CharacterClass> implements ICharacterClassDao {

    @Inject
    private SessionFactory factory;

    @Override
    public SessionFactory getFactory() {
        return factory;
    }
}
