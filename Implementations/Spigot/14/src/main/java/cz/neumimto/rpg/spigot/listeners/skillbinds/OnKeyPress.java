package cz.neumimto.rpg.spigot.listeners.skillbinds;

import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.commands.SkillsCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;

public class OnKeyPress implements Listener {

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SkillService skillService;

    @Inject
    private SkillsCommandFacade commandFacade;

    @EventHandler
    public void onCharacterHeldItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
            return;
        }
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) {
            return;
        }
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey(SpigotInventoryService.SKILLBIND)) {
            String skillName = nbtItem.getString(SpigotInventoryService.SKILLBIND);
            ISpigotCharacter character = characterService.getCharacter(player);
            if (!character.hasCooldown(skillName)) {
                commandFacade.executeSkill(character, skillName);
                player.getInventory().setHeldItemSlot(event.getPreviousSlot());
            }
        }
    }
}
