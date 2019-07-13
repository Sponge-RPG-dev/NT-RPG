package cz.neumimto.rpg.persistance.dao;

import cz.neumimto.core.PersistentContext;
import cz.neumimto.core.Repository;
import cz.neumimto.core.dao.GenericDao;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import org.hibernate.SessionFactory;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 24.2.2019.
 */
@Singleton
@Repository
public class JPACharacterClassDao extends GenericDao<CharacterClass> implements ICharacterClassDao {

    @PersistentContext("nt-rpg")
    private SessionFactory factory;

    @Override
    public SessionFactory getFactory() {
        return factory;
    }
}
