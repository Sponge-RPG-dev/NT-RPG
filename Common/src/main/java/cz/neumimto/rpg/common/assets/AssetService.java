package cz.neumimto.rpg.common.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AssetService {

    public abstract String getAssetAsString(String path);

    public abstract void copyToFile(String s, Path toPath);

    public void copyDefaultClasses(Path path) {
        Path root = path.resolve("skilltrees/");
        try {
            if (!root.toFile().exists()) {
                Files.createDirectory(root);
            }
            path = path.resolve("classes/");
            if (!path.toFile().exists()) {
                Files.createDirectory(path);
            }
            Path sub = path.resolve("primary/");
            if (!sub.toFile().exists()) {
                Files.createDirectory(sub);
            }
            sub = path.resolve("races/");
            if (!sub.toFile().exists()) {
                Files.createDirectory(sub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        copyToFileIfMissing("defaults/skilltrees/magetree.conf", root.resolve("magetree.conf"));
        copyToFileIfMissing("defaults/skilltrees/warriortree.conf", root.resolve("warriortree.conf"));
        copyToFileIfMissing("defaults/classes/primary_classes/Apprentice.conf", path.resolve("primary/Apprentice.conf"));
        copyToFileIfMissing("defaults/classes/primary_classes/Rogue.conf", path.resolve("primary/Rogue.conf"));
        copyToFileIfMissing("defaults/classes/primary_classes/Warrior.conf", path.resolve("primary/Warrior.conf"));
        copyToFileIfMissing("defaults/classes/races/Dwarf.conf", path.resolve("races/Mage.conf"));
        copyToFileIfMissing("defaults/classes/races/Elf.conf", path.resolve("races/Rogue.conf"));
        copyToFileIfMissing("defaults/classes/races/Human.conf", path.resolve("races/Human.conf"));
    }

    private Path tempDirectory;

    public Path getTempWorkingDir() {
        if (tempDirectory == null) {
            try {
                tempDirectory = Files.createTempDirectory("ntrpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempDirectory;
    }

    protected void copyToFileIfMissing(String internal, Path path) {
        if (!Files.exists(path)) {
            copyToFile(internal, path);
        }
    }

    public abstract void copyDefaultGuis(Path workingDir);

    public void copyDefaultLocalizations(Path workingDir) {
        Path root = workingDir.resolve("localizations/");
        if (!Files.isDirectory(root)) {
            try {
                Files.createDirectory(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        copyToFileIfMissing("localizations/core_localization_cs.properties", root.resolve("core_localization_cs.properties"));
        copyToFileIfMissing("localizations/core_localization_en.properties", root.resolve("core_localization_en.properties"));
        copyToFileIfMissing("localizations/core_localization_pl.properties", root.resolve("core_localization_pl.properties"));
    }
}
