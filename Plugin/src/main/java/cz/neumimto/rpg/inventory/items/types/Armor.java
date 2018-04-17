package cz.neumimto.rpg.inventory.items.types;

import cz.neumimto.rpg.effects.IEffectSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.WornEquipmentType;

public class Armor extends CustomItem {

    private WornEquipmentType equipmentType;

    public Armor(ItemStack itemStack, IEffectSource effectSource, WornEquipmentType equipmentType) {
        super(itemStack, effectSource);
        this.equipmentType = equipmentType;
    }

    public WornEquipmentType getEquipmentType() {
        return equipmentType;
    }
}
