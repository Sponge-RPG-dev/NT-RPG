package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Singleton
@CommandAlias("char|c")
public class SpigotCharacterCommands extends BaseCommand {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SpigotGui spigotGui;

    @Subcommand("create")
    public void createCharacter(Player executor, String name) {
        UUID uuid = executor.getUniqueId();

        characterCommandFacade.commandCreateCharacter(uuid, name, executor.getName(), actionResult -> {
            executor.sendMessage(actionResult.getMessage());
        });
    }

    @Subcommand("switch")
    public void switchCharacter(Player executor, String name) {
        IActiveCharacter character = characterService.getCharacter(executor);

        characterCommandFacade.commandSwitchCharacter(character, name, runnable -> {
            Rpg.get().scheduleSyncLater(runnable);
        });
    }

    @Subcommand("attribute-add")
    public void attributesAdd(Player executor, AttributeConfig a, @Default("false") @Optional boolean ui, @Optional Integer slotMod) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        Integer integer = attributesTransaction.get(a.getId());
        attributesTransaction.put(a.getId(), integer + 1);
        if (ui) {
            spigotGui.refreshAttributeView(executor, character, slotMod, a);
        }
    }

    @Subcommand("spellbook-commit")
    public void spellbookCommit(Player executor) {
        InventoryView openInventory = executor.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            return;
        }
        ISpigotCharacter character = characterService.getCharacter(executor);
        int i = 27;

        String[][] persisted = new String[character.getSpellbook().length -1][character.getSpellbook()[0].length -1];
        for (int w = 0; w < character.getSpellbook().length; w++) {

            ItemStack[] page = character.getSpellbook()[w];
            for (int j = 0; j < page.length - 1; j++) {
                ItemStack item = openInventory.getItem(i);
                if (item == null || item.getType() == Material.AIR || isBlank(item)) {
                    page[j] = null;
                } else {
                    page[j] = item;
                    String displayName = item.getItemMeta().getDisplayName();
                    persisted[w][j] = displayName;
                }
                i++;
            }

        }
        character.getCharacterBase().setSpellbookPages(persisted);
        characterService.putInSaveQueue(character.getCharacterBase());

        Bukkit.dispatchCommand(executor, "char");
    }

    private boolean isBlank(ItemStack item) {
        if (item.getType() == Material.YELLOW_STAINED_GLASS_PANE) {
            NBTItem nbtItem = new NBTItem(item);
            return nbtItem.hasKey("ntrpg.spellbook-empty");
        }
        return false;
    }

    @Subcommand("spell-rotation")
    public void toggleSpellRotation(Player executor, boolean state) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        character.setSpellRotation(state);
    }
}
