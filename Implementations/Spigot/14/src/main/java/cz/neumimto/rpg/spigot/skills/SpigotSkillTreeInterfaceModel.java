package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpigotSkillTreeInterfaceModel implements ISkillTreeInterfaceModel {

    private final int modelId;
    private final Material itemType;
    private final short id;

    public SpigotSkillTreeInterfaceModel(Integer modelId, Material itemType, short id) {
        this.modelId = modelId;
        this.itemType = itemType;
        this.id = id;
    }

    public ItemStack toItemStack() {
        ItemStack of = new ItemStack(itemType, 1);
        ItemMeta itemMeta = of.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setCustomModelData(modelId);
        of.setItemMeta(itemMeta);
        return of;
    }

    public short getId() {
        return id;
    }

}
