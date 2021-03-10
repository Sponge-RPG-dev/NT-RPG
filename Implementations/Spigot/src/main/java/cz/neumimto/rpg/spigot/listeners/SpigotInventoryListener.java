package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
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
            //spellbook commands
            if (nbti.hasKey("ntrpg.spellbook.learnedspell")) {
                String skillName = nbti.getString("ntrpg.spellbook.learnedspell");
                ISpigotCharacter character = spigotCharacterService.getCharacter(whoClicked.getUniqueId());
                PlayerSkillContext playerSkillContext = character.getSkillsByName().get(skillName);
                ItemStack skillbind = inventoryService.createSkillbind(playerSkillContext.getSkillData());
                NBTItem futureIcon = new NBTItem(skillbind);

                futureIcon.setBoolean("ntrpg.spellbook.learnedspell.bindicon", true);
                event.setCursor(futureIcon.getItem());
                event.setResult(Event.Result.DENY);
            }
            if (nbti.hasKey("ntrpg.spellbook.learnedspell.bindicon")) {
                event.setCurrentItem(SpellbookListener.createEmptySlot());
                event.setCursor(null);
                event.setResult(Event.Result.DENY);
            }
            if (nbti.hasKey("ntrpg.spellbook-empty")) {
                ItemStack cursor = event.getCursor();
                if (cursor.getType() == Material.AIR) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                NBTItem nbtItem = new NBTItem(cursor);
                if (nbtItem.hasKey("ntrpg.spellbook.learnedspell.bindicon")) {
                    event.setCurrentItem(cursor);
                    event.setCursor(null);
                }
            }
        }
    }

    @EventHandler
    public void itemEquipEvent(InventoryCloseEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getPlayer())) {
            return;
        }
        HumanEntity player = event.getPlayer();

        if (player instanceof Player) {
            Player p = (Player) player;
            ISpigotCharacter character = spigotCharacterService.getCharacter(player.getUniqueId());

            //player quits
            if (character == null) {
                return;
            }

            if (character.requiresDamageRecalculation()) {

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
        NBTItem nbtItem = new NBTItem(itemDrop.getItemStack());
        if (nbtItem.hasKey(SpigotInventoryService.SKILLBIND)) {
            itemDrop.remove();
        }

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

        character.setRequiresDamageRecalculation(true);
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

            int last = character.getLastHotbarSlotInteraction();

            if (last != selectedSlotIndex) {

                boolean b = prepareItemInHand(player, character, itemStack, selectedSlotIndex, rpgItemType,
                        event.getHand(), itemService, inventoryHandler);
                event.setCancelled(b);
            }
        }

    }

    public static boolean prepareItemInHand(Player player, ISpigotCharacter character, ItemStack itemStack,
                                            int selectedSlotIndex, RpgItemType rpgItemType,
                                            EquipmentSlot hand,
                                            SpigotItemService itemService,
                                            InventoryHandler inventoryHandler) {
        Map<Class<?>, RpgInventory> managedInventory = character.getManagedInventory();
        Map<Integer, ManagedSlot> managedSlots = managedInventory.get(PlayerInventory.class).getManagedSlots();

        if (managedSlots.containsKey(selectedSlotIndex)) {

            if (rpgItemType == null) {
                Optional<RpgItemType> opt = itemService.getRpgItemType(itemStack);
                if (opt.isPresent()) {
                    rpgItemType = opt.get();
                } else {
                    character.setMainHand(null, selectedSlotIndex);
                    character.setLastHotbarSlotInteraction(selectedSlotIndex);
                    character.setRequiresDamageRecalculation(true);
                    return false;
                }
            }
            RpgItemStack rpgItemStack = itemService.getRpgItemStack(rpgItemType, itemStack);


            ManagedSlot managedSlot = managedSlots.get(selectedSlotIndex);
            if (inventoryHandler.handleCharacterEquipActionPre(character, managedSlot, rpgItemStack)) {
                inventoryHandler.handleInventoryInitializationPost(character);
                character.setLastHotbarSlotInteraction(selectedSlotIndex);
                if (hand == EquipmentSlot.HAND) {
                    character.setMainHand(rpgItemStack, selectedSlotIndex);
                } else if (hand == EquipmentSlot.OFF_HAND) {
                    character.setOffHand(rpgItemStack);
                }
                character.setRequiresDamageRecalculation(true);
                return false;
            } else {
                if (player.getGameMode() != GameMode.CREATIVE) {

                    if (hand == EquipmentSlot.HAND) {
                        player.getInventory().setItemInMainHand(null);
                    } else if (hand == EquipmentSlot.OFF_HAND) {
                        player.getInventory().setItemInOffHand(null);
                    }

                    String message = Rpg.get().getLocalizationService().translate(LocalizationKeys.CANNOT_USE_ITEM_CONFIGURATION_REASON);
                    BaseComponent c = TextComponent.fromLegacyText(ChatColor.RED + message)[0];
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, c);

                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

                }
                character.setLastHotbarSlotInteraction(-1);
                character.setRequiresDamageRecalculation(true);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getWhoClicked())) {
            return;
        }

        for (Integer slotId : event.getInventorySlots()) {
            if (inventoryService.isManagedInventory(PlayerInventory.class, slotId) || slotId == OFFHAND_SLOT_ID || (slotId >= 0 && slotId <= 8)) {
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
                        character.setRequiresDamageRecalculation(true);
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
                        character.setRequiresDamageRecalculation(true);
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
                    itemService.checkItemClassRequirements(character, rpgItemStack);

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
            } else {
                character.setRequiresDamageRecalculation(true);
            }
        }

    }
}


