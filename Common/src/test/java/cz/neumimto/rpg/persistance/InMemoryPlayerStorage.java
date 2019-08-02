package cz.neumimto.rpg.persistance;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryPlayerStorage implements IPlayerDao {

    private Map<UUID, CharacterBase> map = new ConcurrentHashMap<>();

    public List<CharacterBase> getPlayersCharacters(UUID uuid) {
        throw new RuntimeException("In mememory storage");
    }

    public CharacterBase fetchCharacterBase(CharacterBase base) {
        throw new RuntimeException("In mememory storage");
    }

    @Override
    public void update(CharacterBase characterBase) {
        map.put(characterBase.getUuid(), characterBase);
    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {
        characterSkill.getCharacterBase().getCharacterSkills().remove(characterSkill);
    }

    public CharacterBase getLastPlayed(UUID uuid) {
        throw new RuntimeException("In mememory storage");
    }

    public CharacterBase getCharacter(UUID player, String name) {
        throw new RuntimeException("In mememory storage");
    }

    public int getCharacterCount(UUID uuid) {
        throw new RuntimeException("In mememory storage");
    }

    /**
     * @param uniqueId
     * @return rows updated
     */
    public int deleteData(UUID uniqueId) {
        throw new RuntimeException("In mememory storage");
    }


    public void createAndUpdate(CharacterBase base) {
        map.put(base.getUuid(), base);
    }

    public int markCharacterForRemoval(UUID player, String charName) {
        throw new RuntimeException("In mememory storage");
    }

    public void removePeristantSkill(CharacterSkill characterSkill) {

    }
}
