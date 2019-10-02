package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.converters.ConfigConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FlatFilePlayerDao implements IPlayerDao {

    private String getCharacterConfigFileName(String charName) {
        return charName.toLowerCase() + ".hocon";
    }

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
        Path pdd = getPlayerDataDirectory(player);
        Path resolve = pdd.resolve(getCharacterConfigFileName(name));
        if (Files.isRegularFile(resolve)) {
            try (FileConfig fileConfig = FileConfig.of(resolve)) {
                boolean r = fileConfig.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false);
                if (r) {
                    return null;
                }
                return ConfigConverter.fromConfig(fileConfig);
            }
        }
        return null;
    }

    @Override
    public int getCharacterCount(UUID uuid) {
        Path resolve = getPlayerDataDirectory(uuid);
        File file = resolve.toFile();
        File[] files = file.listFiles();
        int counter = 0;
        if (files != null) {
            for (File file1 : files) {
                if (file1.isFile() && file1.getName().endsWith(".hocon")) {
                    FileConfig of = FileConfig.of(file1);
                    boolean forRemoval = of.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false);
                    if (!forRemoval) {
                        counter++;
                    }
                    of.close();
                }
            }
        }
        return counter;
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
        Path resolve = getPlayerDataDirectory(player).resolve(getCharacterConfigFileName(charName));
        FileConfig of = FileConfig.of(resolve);
        of.set(ConfigConverter.MARKED_FOR_REMOVAL, true);
        of.close();
        return 1;
    }

    @Override
    public void update(CharacterBase characterBase) {
        characterBase.setUpdated(new Date());
        Path resolve = getPlayerDataDirectory(characterBase.getUuid()).resolve(getCharacterConfigFileName(characterBase.getName()));
        FileConfig of = FileConfig.of(resolve);
        ConfigConverter.toConfig(characterBase, of);
        of.close();
    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {
        update(characterSkill.getCharacterBase());
    }
}
