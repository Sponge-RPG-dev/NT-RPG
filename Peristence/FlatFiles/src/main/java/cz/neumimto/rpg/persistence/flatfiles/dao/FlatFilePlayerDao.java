package cz.neumimto.rpg.persistence.flatfiles.dao;

import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.flatfiles.converters.ConfigConverter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class FlatFilePlayerDao implements IPlayerDao {

    private String getCharacterConfigFileName(String charName) {
        return charName.toLowerCase() + ".hocon";
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
        try {
            return Files.walk(pdd)
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().endsWith(".hocon"))
                    .map(FileConfig::of)
                    .peek(FileConfig::close)
                    .filter(f -> !f.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false))
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
        Path path = getPlayerDataDirectory(uuid);
        try {
            return (int) Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().endsWith(".hocon"))
                    .map(FileConfig::of)
                    .peek(FileConfig::close)
                    .filter(f -> !f.getOrElse(ConfigConverter.MARKED_FOR_REMOVAL, false))
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
