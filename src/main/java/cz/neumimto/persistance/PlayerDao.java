package cz.neumimto.persistance;

import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
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
        Query query = manager.createQuery("SELECT a FROM CharacterBase a WHERE a.uuid=:id order by a.updated");
        query.setParameter("id", uuid);
        List resultList = query.getResultList();
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

    private void flush() {
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();
    }
}
