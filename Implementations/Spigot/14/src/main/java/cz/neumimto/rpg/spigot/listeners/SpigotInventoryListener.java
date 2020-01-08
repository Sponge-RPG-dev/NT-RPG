package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@Singleton
@ResourceLoader.ListenerClass
public class SpigotInventoryListener implements Listener {

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

    private static final int OFFHAND_SLOT_ID = 40;

    @EventHandler 
    public void onInventoryInteract(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            NBTItem nbti = new NBTItem(currentItem);
            if (nbti.hasKey("ntrpg.item-command")) {
                Rpg.get().scheduleSyncLater(() -> {
                    String command = nbti.getString("ntrpg.item-command");
                    Bukkit.dispatchCommand(whoClicked, command);
                });
                event.setResult(Event.Result.DENY);
            }
            if (nbti.hasKey("ntrpg.item-iface")) {
                event.setResult(Event.Result.DENY);
            }
            if (nbti.hasKey(SpigotInventoryService.SKILLBIND)) {
                event.setResult(Event.Result.DENY);
                Rpg.get().scheduleSyncLater(() -> {
                    Bukkit.dispatchCommand(whoClicked, "skill " + currentItem.getItemMeta().getDisplayName());
                });
            }
        }
    }

    @EventHandler
    public void itemEquipEvent(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        if (player instanceof Player) {
            Player p = (Player) player;
            ISpigotCharacter character = spigotCharacterService.getCharacter(player.getUniqueId());
            if (character.requiresDamageRecalculation()) {

            }
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        IActiveCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

    /*    if (player.getOpenInventory() != null) {
            return;
        }
*/
        int selectedSlotIndex = player.getInventory().getHeldItemSlot();
        Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
        RpgInventory rpgInventory = managedInventory.get(PlayerInventory.class);
        if (rpgInventory.getManagedSlots().containsKey(selectedSlotIndex)) {
            ManagedSlot currentHand = rpgInventory.getManagedSlots().get(selectedSlotIndex);
            Optional<RpgItemStack> content = currentHand.getContent();
            content.ifPresent(i -> {
                inventoryHandler.handleCharacterUnEquipActionPost(character, currentHand);
                character.setRequiresDamageRecalculation(true);
                character.setMainHand(null, -1);
            });
        }

    }

    @EventHandler(ignoreCancelled = false)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        ItemStack futureMainHand = event.getMainHandItem();
        ItemStack futureOffHand = event.getOffHandItem();
        if (futureMainHand.getType() == Material.AIR && futureOffHand.getType() == Material.AIR) {
            return;
        }

        Player player = event.getPlayer();

        IActiveCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
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

            if ((futureOff == null || (inventoryHandler.isValidItemForSlot(offHandSlotO, futureOff))) &&
                    (futureMain == null || inventoryHandler.isValidItemForSlot(managedSlotM, futureMain))
            ) {
                offHandSlotO.setContent(futureOff);
                managedSlotM.setContent(futureMain);
                character.setRequiresDamageRecalculation(true);
            } else {
                event.setCancelled(true);
            }
        }

        character.setRequiresDamageRecalculation(true);
    }



    @EventHandler
    public void onHotbarInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ISpigotCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

        ItemStack itemStack = event.getItem();
        Optional<RpgItemStack> rpgItemStack = itemService.getRpgItemStack(itemStack);
        if (rpgItemStack.isPresent() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            RpgItemStack r = rpgItemStack.get();
            if (r.getItemType().getItemClass() == ItemClass.ARMOR) {
                event.setCancelled(true);
                return;
            }
        }


        PlayerInventory inventory = player.getInventory();

        int selectedSlotIndex = inventory.getHeldItemSlot();

        if (itemStack.getType() != Material.AIR) {
            if (rpgItemStack.isPresent()) {
                RpgItemStack rpgItemType1 = rpgItemStack.get();

                int last = character.getLastHotbarSlotInteraction();
                if (selectedSlotIndex != last) {
                    Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
                    Map<Integer, ManagedSlot> managedSlots = managedInventory.get(inventory.getClass()).getManagedSlots();

                    if (managedSlots.containsKey(selectedSlotIndex)) {
                        ManagedSlot managedSlot = managedSlots.get(selectedSlotIndex);
                        if (inventoryHandler.handleCharacterEquipActionPre(character, managedSlot, rpgItemType1)) {
                            inventoryHandler.handleInventoryInitializationPost(character);
                            character.setLastHotbarSlotInteraction(selectedSlotIndex);
                            character.setMainHand(rpgItemType1, selectedSlotIndex);
                        } else {
                            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                            inventory.setItemInMainHand(null);
                            character.setLastHotbarSlotInteraction(-1);
                            event.setCancelled(true);
                            character.setRequiresDamageRecalculation(true);
                        }
                    }
                }
            } else {
                character.setMainHand(null, -1);
                character.setLastHotbarSlotInteraction(-1);
                character.setRequiresDamageRecalculation(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        for (Integer slotId : event.getInventorySlots()) {
            if (inventoryService.isManagedInventory(PlayerInventory.class, slotId) || slotId == OFFHAND_SLOT_ID || (slotId >= 0 && slotId <= 8)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            int slotId = event.getSlot();
            if (!inventoryService.isManagedInventory(PlayerInventory.class, slotId)) {
                return;
            }
            IActiveCharacter character = spigotCharacterService.getCharacter(whoClicked.getUniqueId());
            Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
            RpgInventory rpgInventory = managedInventory.get(PlayerInventory.class);
            ManagedSlot managedSlot = rpgInventory.getManagedSlots().get(slotId);

            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

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
}


