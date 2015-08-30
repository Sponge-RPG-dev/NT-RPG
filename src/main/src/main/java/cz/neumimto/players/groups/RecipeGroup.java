package cz.neumimto.players.groups;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class RecipeGroup {
    private String name;
    private Set<ItemStack> set = new HashSet<>();

    public RecipeGroup(String name) {
        this.name = name;
    }

    public Set<ItemStack> getSet() {
        return set;
    }

    public String getName() {
        return name;
    }

    public boolean canCraft(ItemStack i) {
        return set.contains(i);
    }
}
