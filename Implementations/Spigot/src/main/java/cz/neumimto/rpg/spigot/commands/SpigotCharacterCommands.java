package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import cz.neumimto.rpg.spigot.gui.inventoryviews.CharacterAttributesGuiView;
import cz.neumimto.rpg.spigot.items.RPGItemMetadataKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Stack;
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
    public void attributesAdd(Player executor, AttributeConfig a) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        Integer integer = attributesTransaction.get(a.getId());
        attributesTransaction.put(a.getId(), integer + 1);
    }

    @Subcommand("spellbook-commit")
    public void spellbookCommit(Player executor) {
        InventoryView openInventory = executor.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            return;
        }
        ISpigotCharacter character = characterService.getCharacter(executor);
        int i = 27;

        String[][] persisted = new String[character.getSpellbook().length - 1][character.getSpellbook()[0].length - 1];
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
            return item.getItemMeta().getPersistentDataContainer().has(RPGItemMetadataKeys.SPELLBOOKEMPTY);
        }
        return false;
    }

    @Subcommand("spell-rotation")
    public void toggleSpellRotation(Player executor, boolean state) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        character.setSpellRotation(state);
    }

    @Private
    @Subcommand("back")
    public void back(Player executor, @Optional String arg) {
        ISpigotCharacter character = characterService.getCharacter(executor);
        Stack<String> list = character.getGuiCommandHistory();
        if (!list.empty()) {
            list.pop();
            if (!list.empty()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(),
                        () -> Bukkit.dispatchCommand(executor, list.pop()),
                        1L);
            }
        }
        if ("--reset-attributes".equalsIgnoreCase(arg)) {
            characterService.getCharacter(executor).getAttributesTransaction().clear();
        }
        if ("--apply-attribute-tx".equalsIgnoreCase(arg)) {
            characterCommandFacade.commandCommitAttribute(character);
            CharacterAttributesGuiView.clearCache(executor);
        }
        if ("--close-inv".equalsIgnoreCase(arg)) {
            InventoryView openInventory = executor.getOpenInventory();
            openInventory.close();
        }
    }
}
