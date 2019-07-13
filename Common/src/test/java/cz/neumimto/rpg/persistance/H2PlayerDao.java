package cz.neumimto.rpg.persistance;

import cz.neumimto.rpg.common.persistance.dao.JPAPlayerDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class H2PlayerDao extends JPAPlayerDao {

    @Inject
    private SessionFactory sessionFactory;

    @Override
    public SessionFactory getFactory() {
        return sessionFactory;
    }
}
