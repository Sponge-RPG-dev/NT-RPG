package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.persistance.H2PlayerDao;
import cz.neumimto.rpg.TestHibernateConnection;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import org.hibernate.SessionFactory;

public class H2TestGuiceModule extends TestGuiceModule {

    @Override
    protected void configure() {
        super.configure();
        bind(SessionFactory.class).toProvider(TestHibernateConnection::get);
    }

    @Override
    protected Class<? extends PlayerDao> getPlayerDaoImpl() {
        return H2PlayerDao.class;
    }
}
