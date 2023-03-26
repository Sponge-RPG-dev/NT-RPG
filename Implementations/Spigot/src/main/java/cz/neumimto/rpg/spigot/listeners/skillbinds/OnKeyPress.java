package cz.neumimto.rpg.spigot.listeners.skillbinds;

import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.inject.Inject;

public class OnKeyPress implements Listener {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SkillService skillService;

    @Inject
    private SkillsCommandFacade commandFacade;

    @Inject
    private LocalizationService localizationService;

    @EventHandler
    public void onCharacterHeldItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItem(event.getNewSlot());
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        var key = new NamespacedKey(SpigotRpgPlugin.getInstance(), SpigotInventoryService.SKILLBIND);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String skillId = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            ISpigotCharacter character = characterService.getCharacter(player);
            commandFacade.executeSkill(character, skillId);
            int previousSlot = event.getPreviousSlot();
            ItemStack prevItem = inventory.getItem(previousSlot);
            if (prevItem == null) {
                return;
            }
            itemMeta = prevItem.getItemMeta();
            if (!itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                inventory.setHeldItemSlot(event.getPreviousSlot());
            }
        }
    }
}

