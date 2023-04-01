package cz.neumimto.rpg.spigot.gui;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.items.RPGItemMetadataKeys;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Singleton
@AutoService(IRpgListener.class)
@ResourceLoader.ListenerClass
public class SpellbookListener implements IRpgListener {

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

    public static void addInventory(SpigotCharacter character, Inventory i, Map<String, PlayerSkillContext> sorted) {
        State state = new State(sorted);
        openedInventories.put(i, state);
        drawSpellLine(character, i, state);
    }

    public static void drawSpellLine(SpigotCharacter character, Inventory inv, State state) {
        Set<Map.Entry<String, PlayerSkillContext>> entries = state.sorted.entrySet();
        Iterator<Map.Entry<String, PlayerSkillContext>> iterator = entries.iterator();
        int i = 0;
        while (iterator.hasNext() && i < 17) {
            Map.Entry<String, PlayerSkillContext> next = iterator.next();
            ItemStack itemStack = state.icons.get(next.getKey());
            if (itemStack == null) {
                itemStack = SpigotGuiHelper.toItemStack(character, next.getValue());
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
        return SpigotGuiHelper.unclickableInterface(emptySlotMaterial(), 12345, RPGItemMetadataKeys.BINDICON);
    }

    private static Material emptySlotMaterial() {
        return Material.YELLOW_STAINED_GLASS_PANE;
    }

    public static boolean isInInventory(Inventory topInventory) {
        return openedInventories.containsKey(topInventory);
    }

    public static void commit(SpigotCharacter character, Inventory topInventory) {
        for (int columns = 3; columns < 7; columns++) {
            for (int rows = 0; rows < 9; rows++) {
                int slotId = columns * rows;
                ItemStack item = topInventory.getItem(slotId);
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }
                PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                if (pdc.has(RPGItemMetadataKeys.SPELLBOOKEMPTY)) {
                    character.getSpellbook()[columns][rows] = null;
                    character.getCharacterBase().getSpellbookPages()[columns][rows] = null;
                } else {
                    String skillName = pdc.get(RPGItemMetadataKeys.LEARNED_SPELL, PersistentDataType.STRING);
                    PlayerSkillContext playerSkillContext = character.getSkillsByName().get(skillName);

                    character.getSpellbook()[columns][rows] = item;
                    character.getCharacterBase().getSpellbookPages()[columns][rows] = playerSkillContext.getSkillData().getSkillId();
                }
            }
        }
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
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        State removed = openedInventories.remove(inventory);
    }

}
