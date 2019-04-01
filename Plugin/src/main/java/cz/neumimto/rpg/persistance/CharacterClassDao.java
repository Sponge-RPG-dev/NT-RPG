package cz.neumimto.rpg.persistance;

import cz.neumimto.core.PersistentContext;
import cz.neumimto.core.Repository;
import cz.neumimto.core.dao.GenericDao;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 24.2.2019.
 */
@Singleton
@Repository
public class CharacterClassDao extends GenericDao<CharacterClass> {

    @Inject
    @PersistentContext("nt-rpg")
    private SessionFactory factory;

    @Override
    public SessionFactory getFactory() {
        return factory;
    }
}
