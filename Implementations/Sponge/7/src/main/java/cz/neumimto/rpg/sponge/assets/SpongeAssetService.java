package cz.neumimto.rpg.sponge.assets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.SpongeResourceLoader;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;
import java.nio.file.Path;

@Singleton
public class SpongeAssetService implements AssetService {

    @Inject
    private SpongeRpgPlugin plugin;

    @Override
    public String getAssetAsString(String path) {
        try {
            return Sponge.getAssetManager().getAsset(plugin, path).get().readString();
        } catch (IOException e) {
            Log.error("Could not copy file Skills-Definition.conf into the directory " + SpongeResourceLoader.addonDir, e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void copyToFile(String s, Path toPath) {
        Asset asset = Sponge.getAssetManager().getAsset(plugin, s).get();
        try {
            asset.copyToFile(toPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
