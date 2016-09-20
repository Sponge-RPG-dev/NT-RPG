package cz.neumimto.dei;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 5.7.2016.
 */
public class UpKeep {
    Set<ItemStack> itemStacks = new HashSet<>();

    public Set<ItemStack> getUpkeep() {
        return itemStacks;
    }
}
