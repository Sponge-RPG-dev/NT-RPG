package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.permissions.PermissionService;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class AbstractInventoryService<T extends IActiveCharacter> implements InventoryService<T> {

    @Inject
    private AssetService assetService;

    @Inject
    private ItemService itemService;

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "ItemGroups.conf");
        File f = path.toFile();
        if (!f.exists()) {
            assetService.copyToFile("ItemGroups.conf", path);
        }
    }

}
