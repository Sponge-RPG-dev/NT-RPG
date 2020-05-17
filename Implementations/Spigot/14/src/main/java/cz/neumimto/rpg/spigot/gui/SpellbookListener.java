package cz.neumimto.rpg.spigot.gui;

import co.aikar.commands.annotation.Single;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
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
                itemStack = SpigotGuiHelper.toItemStack(character, next.getValue());
                state.icons.put(next.getKey(), itemStack);
            }
            inv.setItem(i, itemStack);
            i++;
        }
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
}
