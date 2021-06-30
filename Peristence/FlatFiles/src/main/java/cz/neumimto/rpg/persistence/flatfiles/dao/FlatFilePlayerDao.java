package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.converters.ConfigConverter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(IPlayerDao.class)
public class FlatFilePlayerDao implements IPlayerDao {

    private static final String DATA_FORMAT = ".json";

    private String getCharacterConfigFileName(String charName) {
        return charName.toLowerCase() + DATA_FORMAT;
    }

    private Path getPlayerDataDirectory(UUID uuid) {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "storage", uuid.toString());
        if (!Files.exists(path)) {
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
        Path pdd = getPlayerDataDirectory(uuid);
        try (Stream<Path> files = Files.walk(pdd)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(FileConfig::of)
                    .filter(f -> !f.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false))
                    .peek(FileConfig::load)
                    .peek(FileConfig::close)
                    .map(ConfigConverter::fromConfig)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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
            try (FileConfig fileConfig = syncConfig(resolve)) {
                fileConfig.load();
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
        Path pdd = getPlayerDataDirectory(uuid);
        try (Stream<Path> files = Files.walk(pdd)) {
            return (int) files
                    .filter(Files::isRegularFile)
                    .map(FileConfig::of)
                    .filter(f -> !f.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false))
                    .peek(FileConfig::close)
                    .count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteData(UUID uuid) {
        Path path = getPlayerDataDirectory(uuid);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void create(CharacterBase base) {
        base.setCreated(new Date());
        try {
            Path resolve = getPlayerDataDirectory(base.getUuid()).resolve(getCharacterConfigFileName(base.getName()));
            Files.createFile(resolve);
        } catch (IOException e) {
            e.printStackTrace();
        }
        update(base);
    }

    @Override
    public void update(CharacterBase characterBase) {
        characterBase.setUpdated(new Date());
        Path resolve = getPlayerDataDirectory(characterBase.getUuid()).resolve(getCharacterConfigFileName(characterBase.getName()));
        try (FileConfig of = syncConfig(resolve)) {
            ConfigConverter.toConfig(characterBase, of);
            of.save();
        }
    }

    @Override
    public int markCharacterForRemoval(UUID player, String charName) {
        Path resolve = getPlayerDataDirectory(player).resolve(getCharacterConfigFileName(charName));
        try (FileConfig of = syncConfig(resolve)) {
            of.load();
            of.set(ConfigConverter.MARKED_FOR_REMOVAL, true);
            of.save();
        }
        return 1;
    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {
        update(characterSkill.getCharacterBase());
    }

    private FileConfig syncConfig(Path resolve) {
        return FileConfig.builder(resolve).sync().build();
    }
}
