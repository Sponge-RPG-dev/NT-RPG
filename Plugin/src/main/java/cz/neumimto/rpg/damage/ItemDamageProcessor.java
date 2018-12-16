package cz.neumimto.rpg.damage;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public enum ItemDamageProcessor {

    OVERRIDE {
        @Override
        public double process(IActiveCharacter character, RPGItemType type, ItemStackSnapshot itemStack) {
            DataContainer itemData = itemStack.toContainer();
            return ItemStackUtils.readGenericDamageNbt(itemData);
        }
    },
    IGNORE {
        @Override
        public double process(IActiveCharacter character, RPGItemType type, ItemStackSnapshot itemStack) {
            double base = character.getBaseWeaponDamage(type);
            for (Integer i : type.getWeaponClass().getProperties()) {
                base += NtRpgPlugin.GlobalScope.characterService.getCharacterProperty(character, i);
            }
            if (!type.getWeaponClass().getPropertiesMults().isEmpty()) {
                double totalMult = 0;
                for (Integer integer : type.getWeaponClass().getPropertiesMults()) {
                    totalMult += NtRpgPlugin.GlobalScope.characterService.getCharacterProperty(character, integer);
                }
                base *= totalMult;
            }
            return base;
        }
    },
    SUM {
        @Override
        public double process(IActiveCharacter character, RPGItemType type, ItemStackSnapshot itemStack) {
            return IGNORE.process(character, type, itemStack) + OVERRIDE.process(character, type, itemStack);
        }
    },
    MAX {
        @Override
        public double process(IActiveCharacter character, RPGItemType type, ItemStackSnapshot itemStack) {
            return Math.max(IGNORE.process(character, type, itemStack), OVERRIDE.process(character, type, itemStack));
        }
    };

    public abstract double process(IActiveCharacter character, RPGItemType type, ItemStackSnapshot itemStack);
}
