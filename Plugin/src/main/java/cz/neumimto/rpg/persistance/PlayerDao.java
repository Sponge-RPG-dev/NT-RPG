/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.persistance;

import cz.neumimto.core.dao.genericDao.GenericDao;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.players.CharacterBase;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.UUID;

/**
 * Created by NeumimTo on 9.7.2015.
 */
//todo catch exceptions and rollback transactions
@Singleton
public class PlayerDao extends GenericDao<CharacterBase> {


	/**
	 * Returns player's characters ordered by updated time desc
	 *
	 * @param uuid
	 * @return
	 */
	public List<CharacterBase> getPlayersCharacters(UUID uuid) {
		Session session = factory.openSession();
		Query query = session.createQuery("SELECT a FROM CharacterBase a WHERE a.uuid=:id ORDER BY a.updated DESC");
		query.setParameter("id", uuid);
		List list = query.list();
		session.close();
		return list;
	}

	public CharacterBase fetchCharacterBase(CharacterBase base) {
		Session session = factory.openSession();
		CharacterBase cb = (CharacterBase) session.merge(base);
		session.beginTransaction();
		cb.getCharacterSkills();
		cb.getCharacterClasses();
		cb.getBaseCharacterAttribute();
		cb.getCharacterSkills();
		session.getTransaction().commit();
		session.close();
		return cb;
	}

	public CharacterBase getLastPlayed(UUID uuid) {
		Session session = factory.openSession();
		List r = session.createCriteria(CharacterBase.class)
				.add(Restrictions.eq("uuid", uuid.toString()))
				.addOrder(Order.desc("updated"))
				.list();
		session.close();

		if (r.size() == 0)
			return null;
		return (CharacterBase) r.get(0);
	}

	public CharacterBase getCharacter(UUID player, String name) {
		Session s = factory.openSession();
		s.beginTransaction();
		Query query = s.createQuery("SELECT a FROM CharacterBase a WHERE a.uuid=:uuid and a.name=:name");
		query.setParameter("uuid", player);
		query.setParameter("name", name);
		List<CharacterBase> list = query.list();
		s.close();
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public int getCharacterCount(UUID uuid) {
		Session s = factory.openSession();
		s.beginTransaction();
		Query query = null;
		query = s.createQuery("SELECT COUNT(c.id) FROM CharacterBase c WHERE c.uuid=:id");
		query.setParameter("id", uuid);
		int i = query.getFirstResult();
		s.close();
		return i;
	}

	/**
	 * @param uniqueId
	 * @return rows updated
	 */
	public int deleteData(UUID uniqueId) {
		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		int i = -1;
		try {
			Query query = session.createQuery("DELETE FROM CharacterBase where uuid=:uuid");
			query.setParameter("uuid", uniqueId);
			i = query.executeUpdate();
			transaction.commit();
		} catch (Throwable t) {
			transaction.rollback();
		} finally {
			session.close();
		}
		return i;
	}


	public void createAndUpdate(CharacterBase base) {
		Session session = factory.openSession();
		Transaction tx = null;
		tx = session.beginTransaction();
		session.saveOrUpdate(base);
		session.flush();
		tx.commit();
		session.close();
	}

	public void remove(UUID player, String charName) {
		String hql = "delete from CharacterBase where uuid= :uid AND lower(name)= :name";
		Session session = factory.openSession();
		Query query = session.createQuery(hql);
		Transaction transaction = session.beginTransaction();
		try {
			query.setString("uid", player.toString());
			query.setString("name", charName.toLowerCase());
			query.executeUpdate();
			// your code end

			transaction.commit();
		} catch (Throwable t) {
			transaction.rollback();
			throw t;
		}
	}
}
