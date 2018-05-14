package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.inventory.*;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.Optional;

/**
 * Created by NeumimTo on 25.3.2018.
 */
public abstract class PlayerInvHandler implements CatalogType {

    private final String name;
    private final String id;

    public PlayerInvHandler(String name) {
        this.id = "nt-rpg:" + name.toLowerCase();
        this.name = name;
    }

    /**
     * Called after loading main plugin config
     */
    public abstract void initHandler();

    /**
     * Method called when
     *  - Player connects
     *  - Player changes world
     *  - Player gain level
     *  - Player learn/upgrade skill
     *  - Player changes weapon
     *  - Player changes hotbar slot
     *  - Player changes armor/accessories
     *
     *  Item init order is in a way player equiped items
     *  @see cz.neumimto.rpg.players.CharacterBase#inventoryEquipSlotOrder
     *  As last
     *
     * @param character
     */
    public abstract void initializeCharacterInventory(IActiveCharacter character);


    protected boolean checkForSlot(IActiveCharacter character, Inventory slot) {
        Optional<ItemStack> peek = slot.peek();
        if (peek.isPresent()) {
            ItemStack itemStack = peek.get();
            RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
            if (fromItemStack == null) {
                return true;
            }
            if (fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
                return checkForItem(character, itemStack, fromItemStack);
            } else {
                return checkForArmorItem(character, itemStack, fromItemStack);
            }
        }
        return true;
    }

    protected boolean checkForItem(IActiveCharacter character, ItemStack itemStack, RPGItemType itemType) {
        CannotUseItemReson cannotUseItemReson = inventoryService().canUse(itemStack, character, itemType);
        return cannotUseItemReson == CannotUseItemReson.OK;
    }

    protected boolean checkForArmorItem(IActiveCharacter character, ItemStack itemStack, RPGItemType itemType) {
        CannotUseItemReson cannotUseItemReson = inventoryService().canWear(itemStack, character, itemType);
        return cannotUseItemReson == CannotUseItemReson.OK;
    }


    /**
     * A method which
     * - adds enchantments to entity effects cache
     * - todo: apply attribute bonuses
     *
     * @param character player
     * @param query Slot having an item to be equipied
     */
    protected CustomItem initializeItemStack(IActiveCharacter character, Inventory query) {
        ItemStack itemStack = query.peek().get();
        CustomItem customItem = CustomItemFactory.createCustomItem(itemStack, query.getInventoryProperty(SlotIndex.class).get().getValue());
        effectService().applyGlobalEffectsAsEnchantments(customItem.getEffects(), character, customItem); //todo
        return customItem;
    }

    protected void deInitializeItemStack(IActiveCharacter character, Inventory query) {
        SlotIndex slotIndex = query.getInventoryProperty(SlotIndex.class).get();
        CustomItem item = character.getEquipedInventorySlots().get(slotIndex.getValue());
        if (item != null) {
            effectService().removeGlobalEffectsAsEnchantments(item.getEffects().keySet(), character, item);
        }
        character.getEquipedInventorySlots().remove(slotIndex.getValue());
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * @param character character instance
     * @param slot id of clicked slot, the slot is NOT in hotbar row
     * @return true if the click inventory event shall be cancelled
     */
    public boolean processSlotInteraction(IActiveCharacter character, Slot slot) {
        Optional<SlotIndex> inventoryProperty = slot.getInventoryProperty(SlotIndex.class);
        Integer value = inventoryProperty.get().getValue();
        if (inventoryService().getEffectSourceBySlotId(value) != null) {
            CustomItem customItem = character.getEquipedInventorySlots().get(value);
            //item has been taken away from the slot
            if (!slot.peek().isPresent()) {
                if (customItem != null) {
                    character.getEquipedInventorySlots().put(value, null);
                    deInitializeItemStack(character, slot);
                }
                return false;
            } else {
                ItemStack itemStack = slot.peek().get();
                RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
                if (fromItemStack == null)
                    return false;
                boolean canUse = false;
                if (fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
                    canUse = checkForArmorItem(character, itemStack, fromItemStack);
                } else {
                    canUse = checkForItem(character, itemStack, fromItemStack);
                }
                if (!canUse)
                    return true;

                //no item before
                if (customItem == null) {
                    CustomItem ci = initializeItemStack(character, slot);
                    character.getEquipedInventorySlots().put(value, ci);
                } else {
                    deInitializeItemStack(character, slot);
                    CustomItem ci = initializeItemStack(character, slot);
                    character.getEquipedInventorySlots().put(value, ci);
                }
            }
        }
        return false;
    }

    public abstract void onRightClick(IActiveCharacter character, int slot);

    public abstract void onLeftClick(IActiveCharacter character, int slot);


    protected InventoryService inventoryService() {
        return NtRpgPlugin.GlobalScope.inventorySerivce;
    }

    protected ItemService itemService() {
        return NtRpgPlugin.GlobalScope.itemService;
    }

    protected EffectService effectService() {
        return NtRpgPlugin.GlobalScope.effectService;
    }

    protected DamageService damageService() {
        return NtRpgPlugin.GlobalScope.damageService;
    }


}
