package cz.neumimto.rpg.persistence.flatfiles.dao;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;

import java.util.List;
import java.util.UUID;

public class FlatFilePlayerDao implements IPlayerDao {

    @Override
    public List<CharacterBase> getPlayersCharacters(UUID uuid) {
        return null;
    }

    @Override
    public CharacterBase getLastPlayed(UUID uuid) {
        return null;
    }

    @Override
    public CharacterBase getCharacter(UUID player, String name) {
        return null;
    }

    @Override
    public int getCharacterCount(UUID uuid) {
        return 0;
    }

    @Override
    public int deleteData(UUID uniqueId) {
        return 0;
    }

    @Override
    public void create(CharacterBase base) {

    }

    @Override
    public int markCharacterForRemoval(UUID player, String charName) {
        return 0;
    }

    @Override
    public void update(CharacterBase characterBase) {

    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {

    }
}
