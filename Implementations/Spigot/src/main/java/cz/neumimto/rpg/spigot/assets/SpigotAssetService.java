package cz.neumimto.rpg.spigot.assets;

import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.spigot.gui.inventoryviews.ConfigurableInventoryGui;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Singleton
public class SpigotAssetService extends AssetService {

    @Override
    public String getAssetAsString(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("assets/nt-rpg/" + path)) {
            if (is == null) {
                throw new IllegalArgumentException("Unknown path \"assets/nt-rpg/" + path + "\"");
            }
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader bf = new BufferedReader(isr)
            ) {
                return bf.lines().collect(Collectors.joining(System.getProperty("line.separator")));
            }

        } catch (IOException e) {
            Log.error("Could not read file " + path + e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void copyToFile(String s, Path toPath) {
        String assetAsString = getAssetAsString(s);
        try {
            Files.write(toPath, assetAsString.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            Log.error("Could not create file " + toPath, e);
        }
    }

    public void copyDefaultGuis(Path workingDir) {
        Path root = workingDir.resolve("guis/");
        if (!Files.isDirectory(root)) {
            try {
                Files.createDirectory(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ServiceLoader.load(ConfigurableInventoryGui.class, getClass().getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .forEach(a -> {
                    String fileName = a.getFileName();
                    copyToFileIfMissing("gui/" + fileName, root.resolve(fileName));
                });


        copyToFileIfMissing("Resources.conf", root.resolve("Resources.conf"));
    }

}

