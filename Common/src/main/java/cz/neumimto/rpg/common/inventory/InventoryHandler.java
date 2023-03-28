package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.EffectParams;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.permissions.PermissionService;

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

    @Inject
    private PermissionService permissionService;

    @Override
    public boolean handleCharacterEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        return isValidItemForSlot(managedSlot, rpgItemStack) &&
                permissionService.hasPermission(character, rpgItemStack.getItemType().getPermission()) &&
                itemService.checkItemAttributeRequirements(character, rpgItemStack) &&
                itemService.checkItemClassRequirements(character, rpgItemStack) &&
                itemService.checkItemPermission(character, rpgItemStack, EntityHand.OFF.name());
    }

    @Override
    public boolean handleCharacterUnEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        return true;
    }

    @Override
    public boolean handleInventoryInitializationPre(IActiveCharacter character) {
        return !character.isStub();
    }


    @Override
    public void handleCharacterEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        handleCharacterUnEquipActionPost(character, managedSlot);
        managedSlot.setContent(rpgItemStack);

        Map<IGlobalEffect, EffectParams> enchantments = rpgItemStack.getEnchantments();
        effectService.applyGlobalEffectsAsEnchantments(enchantments, character, InternalEffectSourceProvider.INSTANCE);

        Map<AttributeConfig, Integer> bonusAttributes = rpgItemStack.getBonusAttributes();
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

        Map<AttributeConfig, Integer> bonusAttributes = equiped.getBonusAttributes();
        characterService.removeTransientAttributes(bonusAttributes, character);

        managedSlot.setContent(null);
    }

    @Override
    public void handleInventoryInitializationPost(IActiveCharacter character) {
        Map<Class<?>, RpgInventory> managedInventories = character.getManagedInventory();

        for (RpgInventory managedInventory : managedInventories.values()) {

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
    }

    @Override
    public boolean isValidItemForSlot(ManagedSlot futureSlot, RpgItemStack rpgItemStack) {
        return futureSlot.accepts(rpgItemStack);
    }

}
