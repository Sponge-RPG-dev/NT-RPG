package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.persistance.model.CharacterSkill;

import java.util.List;
import java.util.UUID;

public interface IPlayerDao {
    /**
     * Returns player's character ordered by updated time desc
     *
     * @param uuid
     * @return
     */
    List<CharacterBase> getPlayersCharacters(UUID uuid);

    CharacterBase getLastPlayed(UUID uuid);

    CharacterBase getCharacter(UUID player, String name);

    int getCharacterCount(UUID uuid);

    int deleteData(UUID uniqueId);

    void create(CharacterBase base);

    int markCharacterForRemoval(UUID player, String charName);

    void update(CharacterBase characterBase);

    void removePersitantSkill(CharacterSkill characterSkill);

}
