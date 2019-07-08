package cz.neumimto.rpg.assets;

import cz.neumimto.rpg.common.assets.AssetService;

import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class TestAssetService implements AssetService {

    @Override
    public String getAssetAsString(String path) {
        return null;
    }

    @Override
    public void copyToFile(String s, Path toPath) {

    }
}
