package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpigotSkillTreeInterfaceModel implements ISkillTreeInterfaceModel {

    private final int modelId;
    private final Material itemType;
    private final short id;

    private ItemStack cache;
    public SpigotSkillTreeInterfaceModel(Integer modelId, Material itemType, short id) {
        this.modelId = modelId;
        this.itemType = itemType;
        this.id = id;
        cache = SpigotGuiHelper.unclickableInterface(itemType, modelId);

    }

    public ItemStack toItemStack() {
        return cache;
    }

    public short getId() {
        return id;
    }

}
