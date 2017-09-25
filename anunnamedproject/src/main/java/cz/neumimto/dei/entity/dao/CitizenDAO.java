package cz.neumimto.dei.entity.dao;

import cz.neumimto.core.dao.genericDao.GenericDao;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.dei.entity.database.player.Citizen;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

/**
 * Created by NeumimTo on 6.7.2016.
 */
@Singleton
public class CitizenDAO extends GenericDao<Citizen> {

	public Citizen loadOrCreate(UUID uuid) {
		Citizen citizen = null;
		Session session = factory.openSession();
		Query query = session.createQuery("from Citizen a where a.uuid = :id");
		query.setParameter("id", uuid);
		List list = query.list();
		if (list.isEmpty()) {
			citizen = new Citizen();
			citizen.setUuid(uuid);
			Transaction transaction = session.beginTransaction();
			session.save(citizen);
			transaction.commit();
			session.close();
		} else {
			citizen = (Citizen) list.get(0);
		}
		return citizen;
	}
}
