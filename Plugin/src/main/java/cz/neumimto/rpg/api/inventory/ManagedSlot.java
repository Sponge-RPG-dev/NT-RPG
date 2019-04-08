package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.WeaponClass;

import java.util.Optional;
import java.util.function.Predicate;

public interface ManagedSlot {

    int getId();

    Optional<RpgItemStack> getContent();

    default Predicate<WeaponClass> getFilter() {
        return weaponClass -> true;
    }

    void setContent(RpgItemStack rpgItemStack);
}
