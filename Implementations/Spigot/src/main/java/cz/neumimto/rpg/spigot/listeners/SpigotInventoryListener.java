package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.ManagedSlot;
import cz.neumimto.rpg.common.inventory.RpgInventory;
import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import cz.neumimto.rpg.spigot.items.RPGItemMetadataKeys;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@Singleton
@AutoService(IRpgListener.class)
@ResourceLoader.ListenerClass
public class SpigotInventoryListener implements IRpgListener {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SpigotCharacterService spigotCharacterService;

    @Inject
    private SpigotItemService itemService;

    @Inject
    private InventoryHandler inventoryHandler;

    @Inject
    private SpigotInventoryService inventoryService;

    @Inject
    private SkillService skillService;

    @Inject
    private SpigotRpg spigotRpg;

    private static final int OFFHAND_SLOT_ID = 40;

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getWhoClicked())) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();

        //System.out.println(event.getSlot());
        //System.out.println(event.getRawSlot());
        //System.out.println(event.getInventory().getType());
        //System.out.println(event.getSlotType());

        if (currentItem != null && currentItem.getType() != Material.AIR) {
            ItemMeta meta = currentItem.getItemMeta();
            if (meta == null) {
                return;
            }

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            if (pdc.has(RPGItemMetadataKeys.COMMAND)) {
                final String command = pdc.get(RPGItemMetadataKeys.COMMAND, PersistentDataType.STRING);
                Rpg.get().scheduleSyncLater(() -> {
                    Bukkit.dispatchCommand(whoClicked, command);
                });
                event.setResult(Event.Result.DENY);
            }
            if (pdc.has(RPGItemMetadataKeys.IFACE)) {
                event.setResult(Event.Result.DENY);
            }
            //spellbook commands
            if (pdc.has(RPGItemMetadataKeys.LEARNED_SPELL)) {
                String skillName = pdc.get(RPGItemMetadataKeys.LEARNED_SPELL, PersistentDataType.STRING);

                ISpigotCharacter character = spigotCharacterService.getCharacter(whoClicked.getUniqueId());
                PlayerSkillContext playerSkillContext = character.getSkillsByName().get(skillName);
                ItemStack skillbind = inventoryService.createSkillbind(playerSkillContext.getSkillData());

                skillbind.getItemMeta().getPersistentDataContainer().set(RPGItemMetadataKeys.BINDICON, PersistentDataType.BYTE, (byte) 1);
                event.setCursor(skillbind);
                event.setResult(Event.Result.DENY);
            }
            if (pdc.has(RPGItemMetadataKeys.BINDICON)) {
                event.setCurrentItem(SpellbookListener.createEmptySlot());
                event.setCursor(null);
                event.setResult(Event.Result.DENY);
            }
            if (pdc.has(RPGItemMetadataKeys.SPELLBOOKEMPTY)) {
                ItemStack cursor = event.getCursor();
                if (cursor.getType() == Material.AIR) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                ItemMeta itemMeta = cursor.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.getPersistentDataContainer().has(RPGItemMetadataKeys.BINDICON)) {
                        event.setCurrentItem(cursor);
                        event.setCursor(null);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getPlayer())) {
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Player player = event.getPlayer();
        IActiveCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

    /*    if (player.getOpenInventory() != null) {
            return;
        }
*/

        Item itemDrop = event.getItemDrop();
        var key = new NamespacedKey(SpigotRpgPlugin.getInstance(), SpigotInventoryService.SKILLBIND);
        ItemMeta itemMeta = itemDrop.getItemStack().getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            itemDrop.remove();
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getPlayer())) {
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack futureMainHand = event.getMainHandItem();
        ItemStack futureOffHand = event.getOffHandItem();
        if (futureMainHand.getType() == Material.AIR && futureOffHand.getType() == Material.AIR) {
            return;
        }

        Player player = event.getPlayer();

        ISpigotCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

        if (character.isSpellRotationActive()) {
            if (!character.hasCooldown("spellbook-rotation")) {
                inventoryService.rotatePlayerSpellbook(player, character);
                long cd = Rpg.get().getPluginConfig().SPELLBOOK_COOLDOWN + System.currentTimeMillis();
                character.getCooldowns().put("spellbook-rotation", cd);
            } else {
                long cd = System.currentTimeMillis() - character.getCooldown("spellbook-rotation");
                String s = localizationService.translate(LocalizationKeys.SPELLBOOK_ROTATION);
                Gui.sendCooldownMessage(character, s, cd);
            }
            event.setCancelled(true);
            return;
        }

        Optional<RpgItemStack> rpgItemStackOff = itemService.getRpgItemStack(futureOffHand);
        Optional<RpgItemStack> rpgItemStackMain = itemService.getRpgItemStack(futureMainHand);

        if (!rpgItemStackMain.isPresent() && !rpgItemStackOff.isPresent()) {
            return;

        } else {
            Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
            RpgInventory rpgInventory = managedInventory.get(PlayerInventory.class);


            int selectedSlotIndex = player.getInventory().getHeldItemSlot();

            ManagedSlot managedSlotM = rpgInventory.getManagedSlots().get(selectedSlotIndex);
            ManagedSlot offHandSlotO = rpgInventory.getManagedSlots().get(OFFHAND_SLOT_ID);

            RpgItemStack futureOff = rpgItemStackOff.orElse(null);
            RpgItemStack futureMain = rpgItemStackMain.orElse(null);

            if ((futureOff == null || (inventoryHandler.handleCharacterEquipActionPre(character, offHandSlotO, futureOff))) &&
                    (futureMain == null || inventoryHandler.handleCharacterEquipActionPre(character, managedSlotM, futureMain))
            ) {
                offHandSlotO.setContent(futureOff);
                managedSlotM.setContent(futureMain);
            } else {
                event.setCancelled(true);
            }
        }

    }


    @EventHandler
    public void onHotbarInteract(PlayerInteractEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getPlayer())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ISpigotCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

        ItemStack itemStack = event.getItem();
        PlayerInventory inventory = player.getInventory();

        int selectedSlotIndex = inventory.getHeldItemSlot();

        if (itemStack != null) {

            RpgItemType rpgItemType = null;
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Optional<RpgItemType> optType = itemService.getRpgItemType(itemStack);
                if (optType.isPresent()) {
                    rpgItemType = optType.get();
                    if (rpgItemType.getItemClass() == ItemClass.ARMOR) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }


        }

    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getWhoClicked())) {
            return;
        }

        for (Integer slotId : event.getInventorySlots()) {
            if (slotId == OFFHAND_SLOT_ID || (slotId >= 0 && slotId <= 8)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getWhoClicked())) {
            return;
        }
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            Player player = (Player) whoClicked;
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            int slotId = event.getSlot();
            boolean shiftClick = event.isShiftClick();
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (!inventoryService.isManagedInventory(PlayerInventory.class, slotId) || event.isShiftClick()) {
                return;
            }


            IActiveCharacter character = spigotCharacterService.getCharacter(whoClicked.getUniqueId());
            Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
            RpgInventory rpgInventory = managedInventory.get(PlayerInventory.class);
            ManagedSlot managedSlot = rpgInventory.getManagedSlots().get(slotId);


            Optional<RpgItemStack> future = itemService.getRpgItemStack(cursor);
            Optional<RpgItemStack> original = itemService.getRpgItemStack(currentItem);

            if (future.isPresent()) {
                RpgItemStack rpgItemStackF = future.get();
                //change
                if (original.isPresent()) {

                    RpgItemStack rpgItemStackO = original.get();

                    boolean k = inventoryHandler.handleCharacterEquipActionPre(character, managedSlot, rpgItemStackF)
                            && inventoryHandler.handleCharacterUnEquipActionPre(character, managedSlot, rpgItemStackO);
                    if (k) {
                        inventoryHandler.handleCharacterUnEquipActionPost(character, managedSlot);
                        inventoryHandler.handleCharacterEquipActionPost(character, managedSlot, rpgItemStackF);

                    } else {
                        event.setResult(Event.Result.DENY);
                        event.setCancelled(true);
                    }
                } else {
                    //equip
                    if (inventoryHandler.handleCharacterEquipActionPre(character, managedSlot, rpgItemStackF)) {
                        inventoryHandler.handleCharacterEquipActionPost(character, managedSlot, rpgItemStackF);
                    } else {
                        event.setResult(Event.Result.DENY);
                        event.setCancelled(true);
                    }
                }

            } else {
                //unequip slot
                if (original.isPresent()) {
                    RpgItemStack rpgItemStack = original.get();
                    if (inventoryHandler.handleCharacterUnEquipActionPre(character, managedSlot, rpgItemStack)) {
                        inventoryHandler.handleCharacterUnEquipActionPost(character, managedSlot);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getEntity())) {
            return;
        }
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        ISpigotCharacter character = spigotCharacterService.getCharacter(player.getUniqueId());
        Item item = event.getItem();

        PlayerInventory inventory = player.getInventory();

        ItemStack itemStackToBePickedUp = item.getItemStack();
        Optional<RpgItemStack> itemStack = itemService.getRpgItemStack(itemStackToBePickedUp);
        if (itemStack.isPresent()) {
            RpgItemStack rpgItemStack = itemStack.get();

            boolean canUse = itemService.checkItemType(character, rpgItemStack) &&
                    itemService.checkItemAttributeRequirements(character, rpgItemStack) &&
                    itemService.checkItemClassRequirements(character, rpgItemStack) &&
                    itemService.checkItemPermission(character, rpgItemStack);

            if (!canUse) {
                int size = inventory.getSize();
                for (int i = 8; i < size - 1; i++) {
                    ItemStack item1 = inventory.getItem(i);
                    if (item1 == null) {
                        inventory.setItem(i, itemStackToBePickedUp);
                        event.setCancelled(true);
                        item.remove();
                        break;
                    }
                }
            }
        }
    }
}


