package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.players.IActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;


@Singleton
public class InventoryHandler implements CharacterInventoryInteractionHandler {

    @Inject
    private ItemService itemService;

    @Override
    public boolean handleCharacterEquipActionPre(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        return isValidItemForSlot(managedSlot, rpgItemStack) &&
               itemService.checkItemType(character, rpgItemStack) &&
               itemService.checkItemAttributeRequirements(character, rpgItemStack) &&
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
        itemService.removeEquipedItemEffects(rpgItemStack.getEnchantments(), character, managedSlot);
        itemService.removeEquipedItemAttributes(rpgItemStack.getBonusAttributes(), character);
    }

    @Override
    public void handleCharacterUnEquipActionPost(IActiveCharacter character, ManagedSlot managedSlot) {
        Optional<RpgItemStack> content = managedSlot.getContent();
        if (content.isPresent()) {
            RpgItemStack equiped = content.get();
            itemService.removeEquipedItemEffects(equiped.getEnchantments(), character, managedSlot);
            itemService.removeEquipedItemAttributes(equiped.getBonusAttributes(), character);
        }
    }

    @Override
    public void handleInventoryInitializationPost(IActiveCharacter character) {

    }

    @Override
    public boolean isValidItemForSlot(ManagedSlot futureSlot, RpgItemStack rpgItemStack) {
        return futureSlot.getFilter().test(rpgItemStack.getItemType().getWeaponClass());
    }
}
