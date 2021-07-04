package cz.neumimto.rpg.spigot.listeners.skillbinds;

import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey(SpigotInventoryService.SKILLBIND)) {
            String skillName = nbtItem.getString(SpigotInventoryService.SKILLBIND);
            ISpigotCharacter character = characterService.getCharacter(player);
            commandFacade.executeSkill(character, skillName);
            int previousSlot = event.getPreviousSlot();
            ItemStack prevItem = inventory.getItem(previousSlot);
            if (prevItem != null) {
                NBTItem nbtItem1 = new NBTItem(prevItem);
                if (!nbtItem1.hasKey(SpigotInventoryService.SKILLBIND)) {
                    inventory.setHeldItemSlot(event.getPreviousSlot());
                }
            } else {
                inventory.setHeldItemSlot(event.getPreviousSlot());
            }
        }
    }
}
