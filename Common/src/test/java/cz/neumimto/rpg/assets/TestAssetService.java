package cz.neumimto.rpg.assets;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.logging.Log;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Singleton
public class TestAssetService extends AssetService {

    @Override
    public String getAssetAsString(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("assets/nt-rpg/" + path)) {
            try (InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8)) {
                return CharStreams.toString(isr);
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
            Files.write(toPath, assetAsString.getBytes(), StandardOpenOption.CREATE_NEW, StandardOpenOption.DELETE_ON_CLOSE);
        } catch (IOException e) {
            Log.error("Could not create file " + toPath, e);
        }
    }

    @Override
    public void copyDefaultGuis(Path workingDir) {

    }
}
