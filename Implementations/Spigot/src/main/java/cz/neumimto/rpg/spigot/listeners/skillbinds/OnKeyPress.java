package cz.neumimto.rpg.spigot.listeners.skillbinds;

import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import javax.inject.Inject;
import java.util.List;

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
        if (item instanceof Metadatable meta) {
            if (meta.hasMetadata(SpigotInventoryService.SKILLBIND)) {
                List<MetadataValue> value = meta.getMetadata(SpigotInventoryService.SKILLBIND);
                String skillName = value.get(0).asString();
                ISpigotCharacter character = characterService.getCharacter(player);
                commandFacade.executeSkill(character, skillName);
                int previousSlot = event.getPreviousSlot();
                ItemStack prevItem = inventory.getItem(previousSlot);
                if (prevItem instanceof Metadatable m) {
                    if (!m.hasMetadata(SpigotInventoryService.SKILLBIND)) {
                        inventory.setHeldItemSlot(event.getPreviousSlot());
                    }
                } else {
                    inventory.setHeldItemSlot(event.getPreviousSlot());
                }
            }
        }
    }
}
