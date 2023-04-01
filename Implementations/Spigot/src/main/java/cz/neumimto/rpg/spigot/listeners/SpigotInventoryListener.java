package cz.neumimto.rpg.spigot.listeners;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.inventory.CannotUseItemReason;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.gui.SpellbookListener;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
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
import org.bukkit.inventory.EquipmentSlot;
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
    private SpigotInventoryService inventoryService;

    @Inject
    private SkillService skillService;

    @Inject
    private SpigotRpg spigotRpg;

    @Inject
    private PermissionService permissionService;

    @Inject
    private SpigotGui spigotGui;

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

                SpigotCharacter character = spigotCharacterService.getCharacter(whoClicked.getUniqueId());
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
        ActiveCharacter character = spigotCharacterService.getCharacter(player);
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

        SpigotCharacter character = spigotCharacterService.getCharacter(player);
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
            RpgItemStack futureOff = rpgItemStackOff.orElse(null);
            RpgItemStack futureMain = rpgItemStackMain.orElse(null);

            if (!itemService.checkItemPermission(character, futureMain, EquipmentSlot.HAND.name())) {
                event.setCancelled(true);
                spigotGui.sendCannotUseItemNotification(character, "", CannotUseItemReason.CONFIG);
            }

            if (!itemService.checkItemPermission(character, futureOff, EquipmentSlot.OFF_HAND.name())) {
                event.setCancelled(true);
                spigotGui.sendCannotUseItemNotification(character, "", CannotUseItemReason.CONFIG);
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }
        if (spigotRpg.isDisabledInWorld(event.getPlayer())) {
            return;
        }

        SpigotCharacter character = spigotCharacterService.getCharacter(player);
        if (character.isStub()) {
            return;
        }

        itemService.getRpgItemType(itemStack).ifPresent(rpgItemType -> {
            if (!player.hasPermission(rpgItemType.getPermission())) {
                ItemStack item = player.getInventory().getItem(event.getHand());
                player.getInventory().setItem(event.getHand(), null);
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);

                event.setCancelled(true);
            }
        });

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
    public void onItemPickup(EntityPickupItemEvent event) {
        if (spigotRpg.isDisabledInWorld(event.getEntity())) {
            return;
        }
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        SpigotCharacter character = spigotCharacterService.getCharacter(player.getUniqueId());
        Item item = event.getItem();

        PlayerInventory inventory = player.getInventory();

        ItemStack itemStackToBePickedUp = item.getItemStack();
        Optional<RpgItemStack> itemStack = itemService.getRpgItemStack(itemStackToBePickedUp);
        if (itemStack.isPresent()) {
            RpgItemStack rpgItemStack = itemStack.get();

            boolean canUse = itemService.checkItemPermission(character, rpgItemStack, EntityHand.MAIN.name());

            if (!canUse) {
                int size = inventory.getSize();
                for (int i = 8; i < size - 1; i++) {
                    if (i == player.getInventory().getHeldItemSlot()) {
                        continue;
                    }
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


