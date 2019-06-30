package cz.neumimto.rpg.common.assets;

import java.nio.file.Path;

public interface AssetService {

    String getAssetAsString(String path);

    void copyToFile(String s, Path toPath);
}
