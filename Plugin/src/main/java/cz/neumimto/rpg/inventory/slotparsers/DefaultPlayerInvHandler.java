package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.inventory.Armor;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.HotbarObject;
import cz.neumimto.rpg.inventory.HotbarObjectTypes;
import cz.neumimto.rpg.inventory.Weapon;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.Optional;

/**
 * Created by NeumimTo on 26.3.2018.
 */
@Singleton
public class DefaultPlayerInvHandler extends PlayerInvHandler {
    
    
    public DefaultPlayerInvHandler() {
        super("slot_order");
    }

    @Override
    public void initializeHotbar(IActiveCharacter character) {
        Hotbar hotbar = character.getPlayer().getInventory().query(Hotbar.class);
        int slot = 0;
        for (Inventory inventory : hotbar) {
            initializeHotbar(character, slot, (Slot) inventory, hotbar);
            slot++;
        }
    }

    //TODO second hand
    protected void initializeHotbar(IActiveCharacter character, int slot, Slot s, Hotbar hotbar) {
        int selectedSlotIndex = hotbar.getSelectedSlotIndex();
        //very stupid but fast
        Optional<ItemStack> peek = s.peek();

        //unequip
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != null) {
            hotbarObject.onUnEquip(character);
        }

        //parse slot
        if (peek.isPresent()) {
            ItemStack itemStack1 = peek.get();

            HotbarObject hotbarObject0 = inventoryService().getHotbarObject(character, itemStack1);

            if (hotbarObject0 != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject0.setSlot(slot);
                character.getHotbar()[slot] = hotbarObject0;
                CannotUseItemReson reason = inventoryService().canUse(itemStack1, character);
                //cannot use
                if (reason != CannotUseItemReson.OK) {
                    character.getHotbar()[slot] = hotbarObject0;
                    character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, TextHelper.parse(Localization.PLAYER_CANT_USE_HOTBAR_ITEMS));
                    character.getDenyHotbarSlotInteractions()[slot] = true;
                    return;
                } else {
                    character.getDenyHotbarSlotInteractions()[slot] = false;
                }

                //charm is active anywhere in the hotbar
                if (hotbarObject0.getHotbarObjectType() == HotbarObjectTypes.CHARM) {
                    hotbarObject0.onEquip(character);
                    character.getDenyHotbarSlotInteractions()[slot] = false;
                } else if (hotbarObject0.getHotbarObjectType() == HotbarObjectTypes.WEAPON && slot == selectedSlotIndex) {
                    //weapon active only if item is in hand
                    hotbarObject0.onRightClick(character); //simulate player interaction to equip the weapon
                    ((Weapon) hotbarObject0).setCurrent(true);
                }

            } else {
                //consumable/non weapon item
                character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
                character.getDenyHotbarSlotInteractions()[slot] = false;
            }

        } else {
            //empty slot
            character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
            character.getDenyHotbarSlotInteractions()[slot] = false;
        }
        // ??
        initializeSecondHand(character);
    }

    protected void initializeSecondHand(IActiveCharacter character) {
        HotbarObject offHand = character.getOffHand();
        if (offHand != null) {
            offHand.onUnEquip(character);
        }
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.OFF_HAND);
        if (itemInHand.isPresent()) {
            ItemStack itemStack = itemInHand.get();
            HotbarObject hotbarObject = inventoryService().getHotbarObject(character, itemStack);
            if (hotbarObject != null) {

            }
        }
    }

    @Override
    public void initializeArmor(IActiveCharacter character) {
        Optional<ItemStack> chestplate = character.getPlayer().getChestplate();
        ItemStack is = null;
        if (chestplate.isPresent()) {
            is = chestplate.get();
            CannotUseItemReson reason = inventoryService().canWear(is, character);
            if (reason != CannotUseItemReson.OK) {
                character.getPlayer().setChestplate(null);
                inventoryService().dropItem(character, is, reason);
            } else {

                Armor armor = inventoryService().getChestplate(character);

                Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.CHESTPLATE);
                if (armor1 != null) {
                    effectService().removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
                }
                character.getEquipedArmor().put(EquipmentTypes.CHESTPLATE, armor);
                effectService().applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);

            }
        }

        Optional<ItemStack> helmet = character.getPlayer().getHelmet();
        if (helmet.isPresent()) {
            is = helmet.get();
            CannotUseItemReson reason = inventoryService().canWear(is, character);
            if (reason != CannotUseItemReson.OK) {
                character.getPlayer().setHelmet(null);
                inventoryService().dropItem(character, is, reason);
            } else {

                Armor armor = inventoryService().getHelmet(character);

                Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.HEADWEAR);
                if (armor1 != null) {
                    effectService().removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
                }
                character.getEquipedArmor().put(EquipmentTypes.HEADWEAR, armor);
                effectService().applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);

            }
        }
        Optional<ItemStack> boots = character.getPlayer().getBoots();
        if (boots.isPresent()) {
            is = boots.get();
            CannotUseItemReson reason = inventoryService().canWear(is, character);
            if (reason != CannotUseItemReson.OK) {
                character.getPlayer().setBoots(null);
                inventoryService().dropItem(character, is, reason);
            } else {
                Armor armor = inventoryService().getBoots(character);
                Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.BOOTS);
                if (armor1 != null) {
                    effectService().removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
                }
                character.getEquipedArmor().put(EquipmentTypes.BOOTS, armor);
                effectService().applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);
            }
        }
        Optional<ItemStack> leggings = character.getPlayer().getLeggings();
        if (leggings.isPresent()) {
            is = leggings.get();
            CannotUseItemReson reason = inventoryService().canWear(is, character);
            if (reason != CannotUseItemReson.OK) {
                character.getPlayer().setLeggings(null);

                inventoryService().dropItem(character, is, reason);
            } else {
                Armor armor = inventoryService().getLeggings(character);

                Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.LEGGINGS);
                if (armor1 != null) {
                    effectService().removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
                }
                character.getEquipedArmor().put(EquipmentTypes.LEGGINGS, armor);
                effectService().applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);
            }
        }
    }

    @Override
    public void changeActiveHotbarSlot(IActiveCharacter character, int slot) {
        if (character.getDenyHotbarSlotInteractions()[slot]) {
            HotbarObject hotbarObject = character.getHotbar()[slot];
            if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                if (hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON) {
                   dropItemFromMainHand(character, slot);
                }
                character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, TextHelper.parse(Localization.CANNOT_USE_ITEM_GENERIC));
            }
        } else {
            character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(""));
        }
    }

    @Override
    public void onRightClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (!character.getDenyHotbarSlotInteractions()[slot]) {
            if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject.onRightClick(character);
            }
        } else if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE && hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON) {
            dropItemFromMainHand(character, slot);
        }
    }

    @Override
    public void onLeftClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (!character.getDenyHotbarSlotInteractions()[slot]) {
            if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject.onLeftClick(character);
            }
        } else if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE && hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON) {
            dropItemFromMainHand(character, slot);
        }
    }

    protected void dropItemFromMainHand(IActiveCharacter character, int slot) {
        character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
        ItemStack itemStack = itemInHand.get().copy();
        character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, null);
        ItemStackUtils.dropItem(character.getPlayer(), itemStack);
    }
}
