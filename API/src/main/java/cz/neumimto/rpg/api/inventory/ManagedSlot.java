package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.RpgItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public interface ManagedSlot {

    int getId();

    Optional<RpgItemStack> getContent();

    default Predicate<RpgItemStack> getFilter() {
        return weaponClass -> true;
    }

    default boolean accepts(RpgItemStack rpgItemStack) {
        return getFilter().test(rpgItemStack);
    }

    void setContent(RpgItemStack rpgItemStack);
}
