package cz.neumimto.rpg.spigot.gui;


import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.common.gui.GuiParser;
import cz.neumimto.rpg.common.gui.InventorySlotProcessor;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SpigotUIReader extends GuiParser<ItemStack, Inventory> {

    public Map<String, Object> initInventories() {
        return super.initInventories(SpigotUIReader.class.getClassLoader(), "guis.conf");
    }

    @Override
    protected InventorySlotProcessor<ItemStack, Inventory> getInventorySlotProcessor() {
        return (item, inventory, slotId) -> inventory.setItem(slotId, item);
    }

    @Override
    protected ItemStack classTypeButton(Map.Entry<String, ClassTypeDefinition> entry) {
        return SpigotGuiHelper.button(Material.CRAFTING_TABLE,
                ChatColor.valueOf(entry.getValue().getPrimaryColor()) + entry.getKey(),
                "ninfo classes " + entry.getKey(), entry.getValue().getModelId());
    }

    @Override
    protected ItemStack toItemStack(ClassDefinition a) {
        return SpigotGuiHelper.toSpellbookItemStack(a, "");
    }

    @Override
    protected ItemStack itemStringToItemStack(String[] split, Supplier<String> command) {
        Material material = Material.matchMaterial(split[2]);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        int i1 = Integer.parseInt(split[3]);
        if (i1 > 0) {
            itemMeta.setCustomModelData(i1);
        }

        itemMeta.setDisplayName(Rpg.get().getLocalizationService().translate(split[1]));
        itemStack.setItemMeta(itemMeta);
        String cmd = command.get();
        if ("".equals(cmd) || "---".equalsIgnoreCase(cmd)) {
            itemStack = SpigotGuiHelper.unclickableInterface(itemStack);
        } else {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setString("ntrpg.item-command", cmd);
            itemStack = nbtItem.getItem();
        }
        return itemStack;
    }

    @Override
    protected ItemStack toItemStack(ClassItem weapon) {
        SpigotRpgItemType type = (SpigotRpgItemType) weapon.getType();
        ItemStack itemStack = new ItemStack(type.getMaterial());
        double damage = weapon.getDamage();

        if (damage <= 0) {
            damage = type.getDamage();
        }
        ChatColor colorByDamage = ChatColor.RED;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (type.getModelId() != null) {
            itemMeta.setCustomModelData(Integer.parseInt(type.getModelId()));
        }
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        String damageStr = localizationService.translate(LocalizationKeys.ITEM_DAMAGE);
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GRAY + damageStr + ": " + colorByDamage + damage);
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
        return SpigotGuiHelper.unclickableInterface(itemStack);
    }

    @Override
    protected Inventory createInventory(String preferedColor, String header) {
        ChatColor c = ChatColor.WHITE;
        if (preferedColor != null) {
            c = ChatColor.valueOf(preferedColor.toUpperCase());
        }
        String translate = Rpg.get().getLocalizationService().translate(header);
        return Bukkit.createInventory(null, 6 * 9, c + translate);
    }
}
