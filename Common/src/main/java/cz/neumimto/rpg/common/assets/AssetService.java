package cz.neumimto.rpg.common.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AssetService {

    public abstract String getAssetAsString(String path);

    public abstract void copyToFile(String s, Path toPath);

    public void copyDefaults(Path path) {
        Path root = path.resolve("skilltrees/");
        try {
            Files.createDirectory(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
        copyToFile("defaults/skilltrees/magetree.conf", root.resolve("magetree.conf"));
        copyToFile("defaults/skilltrees/warriortree.conf", root.resolve("warriortree.conf"));

        try {
            path = path.resolve("classes/");
            Files.createDirectory(path);
            Files.createDirectory(path.resolve("primary/"));
            Files.createDirectory(path.resolve("races/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        copyToFile("defaults/classes/primary_classes/Apprentice.conf", path.resolve("primary/Apprentice.conf"));
        copyToFile("defaults/classes/primary_classes/Rogue.conf", path.resolve("primary/Rogue.conf"));
        copyToFile("defaults/classes/primary_classes/Warrior.conf", path.resolve("primary/Warrior.conf"));
        copyToFile("defaults/classes/races/Dwarf.conf", path.resolve("races/Mage.conf"));
        copyToFile("defaults/classes/races/Elf.conf", path.resolve("races/Rogue.conf"));
        copyToFile("defaults/classes/races/Human.conf", path.resolve("races/Human.conf"));
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
}
