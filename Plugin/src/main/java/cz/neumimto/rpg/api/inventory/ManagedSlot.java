package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;

import java.util.Optional;
import java.util.function.Predicate;

public interface ManagedSlot {

    int getId();

    Optional<RpgItemStack> getContent();

    default Predicate<WeaponClass> getFilter() {
        return weaponClass -> true;
    }

    default boolean accepts(RpgItemStack rpgItemStack) {
        return getFilter().test(rpgItemStack.getItemType().getWeaponClass());
    }

    default boolean accepts(RpgItemType rpgItemType) {
        return getFilter().test(rpgItemType.getWeaponClass());
    }

    void setContent(RpgItemStack rpgItemStack);
}
