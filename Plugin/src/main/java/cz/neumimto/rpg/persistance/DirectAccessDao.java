package cz.neumimto.rpg.persistance;

import cz.neumimto.core.dao.GenericDao;
import cz.neumimto.core.ioc.Singleton;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Map;


/**
 * Created by ja on 8.10.2016.
 */
@Singleton
public class DirectAccessDao extends GenericDao {


	public <T> T findUnique(Class<T> t, String query, Map<String, Object> param) {
		Session session = factory.openSession();
		Query q = session.createQuery(query);
		for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
			q.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
		}
		T result = (T) q.uniqueResult();
		session.close();
		return result;
	}

	public <T> List<T> findList(Class<T> t, String query, Map<String, Object> param) {
		Session session = factory.openSession();
		Query q = session.createQuery(query);
		for (Map.Entry<String, Object> stringObjectEntry : param.entrySet()) {
			q.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
		}
		List result = q.list();
		session.close();
		return result;
	}

	public void update(String hql, Map<String, Object> param) {
		Session session = factory.openSession();
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
}
