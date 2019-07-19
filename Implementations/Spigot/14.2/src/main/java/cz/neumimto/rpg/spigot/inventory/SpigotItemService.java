package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import org.bukkit.Material;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class SpigotItemService extends AbstractItemService {

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass wClass) {
        Material type = Material.getMaterial(parsed.itemId);
        if (type == null) {
            Log.error(" - Not Managed ItemType " + parsed.itemId);
            return Optional.empty();
        }

        return Optional.of(new SpigotRpgItemType(parsed.itemId, parsed.variant, wClass, parsed.damage, parsed.armor, type));
    }
}
