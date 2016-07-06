package cz.neumimto.dei.entity.dao;

import cz.neumimto.core.dao.genericDao.GenericDao;
import cz.neumimto.dei.entity.database.worldobject.Nation;
import cz.neumimto.dei.entity.database.worldobject.Town;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by NeumimTo on 6.7.2016.
 */
public class TownDAO  extends GenericDao<Town> {

    public List<Town> getAll() {
        Session session = factory.openSession();
        List list = session.createQuery("from Town t").list();
        session.clear();
        return list;
    }

    public List<Town> getAll(Nation nation) {
        Session session = factory.openSession();
        Query query = session.createQuery("from Town t where t.nation = :nation");
        query.setParameter("nation",nation);
        List list = query.list();
        session.close();
        return list;
    }

    public List<Object[]> getAllResidents(Nation nation) {
        Session session = factory.openSession();


        return null;
    }

    public Town getTown(String name) {
        Session session = factory.openSession();
        Query query = session.createQuery("from Town t where t.name=:name");
        query.setParameter("name",name);
        List list = query.list();
        session.close();
        return (Town) list.get(0);
    }

}
