package cz.neumimto.rpg.persistance;

import cz.neumimto.core.dao.genericDao.GenericDao;
import cz.neumimto.core.ioc.Singleton;
import org.hibernate.Query;
import org.hibernate.Session;

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
		session.flush();
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
		session.flush();
		session.close();
		return result;
	}
}
