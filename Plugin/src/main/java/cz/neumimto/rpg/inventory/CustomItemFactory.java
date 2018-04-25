package cz.neumimto.rpg.inventory;

import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;


//Just in case somebody would like to inherit from CustomItem class
@Singleton
public class CustomItemFactory {

    private static CustomItemBuilder builder;

    private static InventoryService inventoryService;

    @PostProcess
    public void initBuilder() {
        builder = new CustomItemBuilder();
        inventoryService = NtRpgPlugin.GlobalScope.inventorySerivce;
        //I should create InjectStatic/Inject lazy some day.
    }


    public static class CustomItemBuilder {

        public CustomItem create(ItemStack itemStack, Integer value) {
            CustomItem customItem = new CustomItem(itemStack, inventoryService.getEffectSourceBySlotId(value));
            if (itemStack.getType() == ItemTypes.NONE) {
                customItem.setEffects(new HashMap<>());
                customItem.setLevel(0);
            } else {
                customItem.setEffects(inventoryService.getItemEffects(itemStack));
                customItem.setLevel(inventoryService.getItemLevel(itemStack));
            }
            return customItem;
        }

    }

    public static CustomItem createCustomItem(ItemStack is, Integer value) {
        return builder.create(is, value);
    }


}
