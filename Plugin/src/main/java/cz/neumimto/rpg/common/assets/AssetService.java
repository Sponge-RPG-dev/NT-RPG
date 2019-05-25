package cz.neumimto.rpg.common.assets;

import java.nio.file.Path;

public interface AssetService {

    public String getAssetAsString(String path);

    void copyToFile(String s, Path toPath);
}
