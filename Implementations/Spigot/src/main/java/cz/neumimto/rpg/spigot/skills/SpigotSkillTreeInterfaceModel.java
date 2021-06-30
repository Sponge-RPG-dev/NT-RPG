package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.spigot.gui.SpigotGuiHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpigotSkillTreeInterfaceModel implements ISkillTreeInterfaceModel {

    private final Material itemType;
    private final short id;

    private ItemStack cache;

    public SpigotSkillTreeInterfaceModel(Integer modelId, Material itemType, short id) {
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
