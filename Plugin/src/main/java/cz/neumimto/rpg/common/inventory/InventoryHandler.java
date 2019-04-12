package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.attributes.Attribute;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;


@Singleton
public class InventoryHandler implements CharacterInventoryInteractionHandler {

    @Inject
    private ItemService itemService;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private EffectService effectService;

    @Inject
    private CharacterService characterService;

    @Override
    public boolean handleCharacterEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        return isValidItemForSlot(managedSlot, rpgItemStack) &&
               itemService.checkItemType(character, rpgItemStack) &&
               itemService.checkItemAttributeRequirements(character, managedSlot, rpgItemStack) &&
               itemService.checkItemClassRequirements(character, rpgItemStack);
    }

    @Override
    public boolean handleCharacterUnEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        return true;
    }

    @Override
    public boolean handleInventoryInitializationPre(IActiveCharacter character) {
        return true;
    }


    @Override
    public void handleCharacterEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        handleCharacterUnEquipActionPost(character, managedSlot);
        managedSlot.setContent(rpgItemStack);

        Map<IGlobalEffect, EffectParams> enchantments = rpgItemStack.getEnchantments();
        effectService.applyGlobalEffectsAsEnchantments(enchantments, character, InternalEffectSourceProvider.INSTANCE);

        Map<Attribute, Integer> bonusAttributes = rpgItemStack.getBonusAttributes();
        characterService.addTransientAttribtues(character, bonusAttributes);
    }

    @Override
    public void handleCharacterUnEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot) {
        Optional<RpgItemStack> content = managedSlot.getContent();
        if (content.isPresent()) {
            RpgItemStack equiped = content.get();

            handleCharacterUnEquipActionPost(character, managedSlot, equiped);
        }
    }

    private void handleCharacterUnEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack equiped) {
        Map<IGlobalEffect, EffectParams> enchantments = equiped.getEnchantments();
        effectService.removeGlobalEffectsAsEnchantments(enchantments.keySet(), character, InternalEffectSourceProvider.INSTANCE);

        Map<Attribute, Integer> bonusAttributes = equiped.getBonusAttributes();
        characterService.removeTransientAttributes(bonusAttributes, character);

        managedSlot.setContent(null);
    }

    @Override
    public void handleInventoryInitializationPost(IActiveCharacter character) {
        RpgInventory managedInventory = character.getManagedInventory();
        character.setRequiresDamageRecalculation(true);

        for (ManagedSlot managedSlot : managedInventory.getManagedSlots().values()) {
            Optional<RpgItemStack> content = managedSlot.getContent();
            if (content.isPresent()) {
                RpgItemStack rpgItemStack = content.get();
                if (isValidItemForSlot(managedSlot, rpgItemStack)) {
                    handleCharacterEquipActionPost(character, managedSlot, rpgItemStack);
                } else {
                    handleCharacterUnEquipActionPost(character, managedSlot, rpgItemStack);
                }
            }
        }
    }

    @Override
    public boolean isValidItemForSlot(ManagedSlot futureSlot, RpgItemStack rpgItemStack) {
        return futureSlot.getFilter().test(rpgItemStack.getItemType().getWeaponClass());
    }
}
