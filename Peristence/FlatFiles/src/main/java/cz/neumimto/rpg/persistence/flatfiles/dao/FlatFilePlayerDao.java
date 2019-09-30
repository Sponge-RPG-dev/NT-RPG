package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.electronwill.nightconfig.core.Config;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.converters.ConfigConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FlatFilePlayerDao implements IPlayerDao {

    private Path getPlayerDataDirectory(UUID uuid) {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "storage", uuid.toString());
        if (Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

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
        base.setCreated(new Date());
        update(base);
    }

    @Override
    public int markCharacterForRemoval(UUID player, String charName) {
        return 0;
    }

    @Override
    public void update(CharacterBase characterBase) {
        characterBase.setUpdated(new Date());
        Config config = ConfigConverter.toConfig(characterBase);
    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {

    }
}
