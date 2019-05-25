package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.core.PersistentContext;
import cz.neumimto.core.Repository;
import cz.neumimto.core.dao.GenericDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;


/**
 * Created by ja on 8.10.2016.
 */
@Singleton
@Repository
public class DirectAccessDao extends GenericDao {

    @PersistentContext("nt-rpg")
    private SessionFactory sessionFactory;

    public <T> T findUnique(Class<T> t, String query, Map<String, Object> param) {
        Session session = getFactory().openSession();
        Query q = session.createQuery(query);
        for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
            q.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        T result = (T) q.uniqueResult();
        session.close();
        return result;
    }

    public <T> List<T> findList(Class<T> t, String query, Map<String, Object> param) {
        Session session = getFactory().openSession();
        Query q = session.createQuery(query);
        for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
            q.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        List result = q.list();
        session.close();
        return result;
    }

    public void update(String hql, Map<String, Object> param) {
        Session session = getFactory().openSession();
        Query query = session.createQuery(hql);
        Transaction transaction = session.beginTransaction();
        try {
            for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
                query.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
            query.executeUpdate();
            transaction.commit();
        } catch (Throwable t) {
            transaction.rollback();

        }
    }

    @Override
    public SessionFactory getFactory() {
        return this.sessionFactory;
    }
}
