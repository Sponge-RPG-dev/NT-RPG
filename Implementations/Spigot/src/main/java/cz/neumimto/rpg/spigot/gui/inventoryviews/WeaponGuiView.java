package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.elements.GuiCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@AutoService(ConfigurableInventoryGui.class)
public class WeaponGuiView extends ConfigurableInventoryGui {

    private static WeaponGuiView instance;
    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private LocalizationService localizationService;


    public WeaponGuiView() {
        super("Weapons.conf");
        this.instance = this;
    }

    private static final Map<UUID, ChestGui> cache = new HashMap<>();

    public static ChestGui get(CommandSender commandSender) {
        Player player = (Player) commandSender;
        UUID uniqueId = player.getUniqueId();
        if (cache.containsKey(uniqueId)) {
            return cache.get(uniqueId);
        }
        ChestGui gui = instance.loadGui(player);
        cache.put(uniqueId, gui);
        return gui;
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    @Override
    public void clearCache(UUID uuid) {
        cache.remove(uuid);
    }

    @Override
    public Map<String, List<GuiCommand>> getPaneData(CommandSender commandSender) {
        Map<String, List<GuiCommand>> map = new HashMap<>();
        List<GuiCommand> list = new ArrayList<>();
        if (commandSender instanceof Player player) {
            ISpigotCharacter character = characterService.getCharacter(player);
            var weapons = character.getAllowedWeapons();
            for (Map.Entry<RpgItemType, Double> e : weapons.entrySet()) {
                ItemStack itemStack = toItemStack(e.getKey(), e.getValue());
                list.add(new GuiCommand(itemStack));
            }
        }
        map.put("Weapons", list);
        return map;
    }

    private ItemStack toItemStack(RpgItemType key, double damage) {
        Material material = Material.matchMaterial(key.getId());
        List<String> lore = new ArrayList<>();
        ItemStack is = new ItemStack(material);

        String translate = localizationService.translate(LocalizationKeys.ITEM_CLASS);
        lore.add(ChatColor.GRAY + translate + ": " + ChatColor.GREEN + key.getItemClass().getName());
        if (damage != 0) {
            translate = localizationService.translate(LocalizationKeys.ITEM_DAMAGE);
            lore.add(ChatColor.GRAY + translate + ": " + ChatColor.RED + damage);
        }
        if (Rpg.get().getPluginConfig().DEBUG.isBalance() && key.getModelId() != null && !key.getModelId().isEmpty()) {
            lore.add(ChatColor.DARK_GRAY + "DEBUG:: CustomModelData:" + key.getModelId());
        }
        ItemMeta itemMeta = is.getItemMeta();
        if (key.getModelId() != null) {
            itemMeta.setCustomModelData(Integer.valueOf(key.getModelId()));
        }
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        return is;
    }
}
