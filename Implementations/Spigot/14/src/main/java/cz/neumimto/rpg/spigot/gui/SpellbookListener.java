package cz.neumimto.rpg.spigot.gui;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.events.skill.SpigotHealEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Singleton
@ResourceLoader.ListenerClass
public class SpellbookListener implements Listener {

    @Inject
    private CharacterService characterService;

    private static final Map<Inventory, State> openedInventories = new HashMap<>();

    private static ItemStack btnDisable;
    private static ItemStack btnEnable;
    private static ItemStack btnCommit;
    private static ItemStack btnBack;
    private static ItemStack addPage;

    public static void initBtns() {
        btnBack = SpigotGuiHelper.button(Material.PAPER,
                Rpg.get().getLocalizationService().translate(LocalizationKeys.BACK), "char");
        btnDisable = SpigotGuiHelper.button(Material.PAPER,
                Rpg.get().getLocalizationService().translate(LocalizationKeys.SPELLBOOK_DISABLE), "char spell-rotation false");
        btnEnable = SpigotGuiHelper.button(Material.PAPER,
                Rpg.get().getLocalizationService().translate(LocalizationKeys.SPELLBOOK_ENABLE), "char spell-rotation true");
        btnCommit = SpigotGuiHelper.button(Material.PAPER,
                Rpg.get().getLocalizationService().translate(LocalizationKeys.SPELLBOOK_COMMIT), "char spellbook-commit");
        addPage = SpigotGuiHelper.button(Material.PAPER,
                Rpg.get().getLocalizationService().translate(LocalizationKeys.SPELLBOOK_ADDPAGE), "char spellbook-add-page");
    }

    public static void addInventory(ISpigotCharacter character, Inventory i, Map<String, PlayerSkillContext> sorted) {
        State state = new State(sorted);
        openedInventories.put(i, state);
        drawSpellLine(character, i, state);
    }

    public static void drawSpellLine(ISpigotCharacter character, Inventory inv, State state) {
        Set<Map.Entry<String, PlayerSkillContext>> entries = state.sorted.entrySet();
        Iterator<Map.Entry<String, PlayerSkillContext>> iterator = entries.iterator();
        int i = 0;
        while (iterator.hasNext() && i < 17) {
            Map.Entry<String, PlayerSkillContext> next = iterator.next();
            ItemStack itemStack = state.icons.get(next.getKey());
            if (itemStack == null) {
                itemStack = SpigotGuiHelper.toSpellbookItemStack(character, next.getValue());
                state.icons.put(next.getKey(), itemStack);
            }
            inv.setItem(i, itemStack);
            i++;
        }

        while (i != 18) {
            inv.setItem(i, createInterfaceIcon());
            i++;
        }

        inv.setItem(i, btnBack);
        i++;

        ItemStack is = character.isSpellRotationActive() ? btnDisable : btnEnable;
        inv.setItem(i, is);
        i++;

        inv.setItem(i, createInterfaceIcon());
        i++;

        inv.setItem(i, createInterfaceIcon());
        i++;

        inv.setItem(i, btnCommit);
        i++;

        inv.setItem(i, createInterfaceIcon());
        i++;

        inv.setItem(i, createInterfaceIcon());
        i++;

        inv.setItem(i, createInterfaceIcon());
        i++;

        inv.setItem(i, addPage);
        i++;

        ItemStack[][] spellbook = character.getSpellbook();
        for (int j = 0; j < spellbook.length; j++) {
            for (int k = 0; k < spellbook[0].length; k++) {
                ItemStack stack = spellbook[j][k];
                if (stack == null) {
                    inv.setItem(i, createEmptySlot());
                } else {
                    inv.setItem(i, stack);
                }
                i++;
            }
        }
    }

    private static ItemStack createInterfaceIcon() {
        return SpigotGuiHelper.unclickableInterface(Material.GRAY_STAINED_GLASS_PANE, 12345);
    }

    public static ItemStack createEmptySlot() {
        return SpigotGuiHelper.unclickableInterface(Material.YELLOW_STAINED_GLASS_PANE, 12345, "ntrpg.spellbook-empty");
    }

    public static ItemStack toBindIcon(ItemStack toBeCloned, String name) {
        ItemStack next = new ItemStack(toBeCloned.getType());
        ItemMeta itemMeta = toBeCloned.getItemMeta();
        ItemMeta itemMeta1 = next.getItemMeta();
        if (itemMeta.hasCustomModelData()){
            itemMeta1.setCustomModelData(itemMeta.getCustomModelData());
        }
        itemMeta1.setDisplayName(ChatColor.GOLD + name);
        next.setItemMeta(itemMeta1);
        return next;
    }

    private static class State {

        private Map<String, PlayerSkillContext> sorted;
        private Map<String, ItemStack> icons;
        public State(Map<String, PlayerSkillContext> sorted) {
            icons = new HashMap<>();
            this.sorted = sorted;
        }
    }


    @EventHandler
    public void onSpellBookClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        Inventory clickedInventory = event.getClickedInventory();
        State state = openedInventories.get(clickedInventory);
        if (state == null) {
            return;
        }
        HumanEntity humanEntity = clickedInventory.getViewers().get(0);
        IActiveCharacter character = characterService.getCharacter(humanEntity.getUniqueId());

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        State removed = openedInventories.remove(inventory);
        if (removed != null) {

        }

    }


}
