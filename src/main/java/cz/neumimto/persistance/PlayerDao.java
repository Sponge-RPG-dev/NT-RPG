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

package cz.neumimto.persistance;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.players.CharacterBase;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 9.7.2015.
 */
@Singleton
public class PlayerDao {

    @Inject
    private EntityManager manager;


    /**
     * Returns player's characters ordered by updated time desc
     *
     * @param uuid
     * @return
     */
    public List<CharacterBase> getPlayersCharacters(UUID uuid) {
        Query query = manager.createQuery("SELECT a FROM CharacterBase a WHERE a.uuid=:id");
        query.setParameter("id", uuid);
        List resultList = query.getResultList();
        System.out.println(resultList.size());
        return resultList;
    }

    public void update(CharacterBase characterBase) {
        manager.getTransaction().begin();
        manager.merge(characterBase);
        manager.getTransaction().commit();
    }

    public void save(CharacterBase characterBase) {
        manager.getTransaction().begin();
        manager.persist(characterBase);
        manager.getTransaction().commit();
    }


    public void deleteData(UUID uniqueId, Consumer<Integer> consumer) {
        manager.getTransaction().begin();
        Query q = manager.createQuery("DELETE FROM CharacterBase a WHERE a.uuid=:id");
        q.setParameter("id", uniqueId.toString());
        int i = q.executeUpdate();
        manager.getTransaction().commit();
        consumer.accept(i);
    }

    public int getCharacterCount(UUID uuid) {
        Query query = manager.createQuery("SELECT COUNT(c.id) FROM CharacterBase c WHERE c.uuid=:id");
        query.setParameter("id", uuid);
        return (int) query.getSingleResult();
    }
}
