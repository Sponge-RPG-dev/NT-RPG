package cz.neumimto.rpg.api.inventory;

import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;

import java.util.Optional;
import java.util.function.Predicate;

public interface ManagedSlot {

    int getId();

    Optional<RpgItemStack> getContent();

    default Predicate<ItemClass> getFilter() {
        return weaponClass -> true;
    }

    default boolean accepts(RpgItemStack rpgItemStack) {
        return getFilter().test(rpgItemStack.getItemType().getItemClass());
    }

    default boolean accepts(RpgItemType rpgItemType) {
        return getFilter().test(rpgItemType.getItemClass());
    }

    void setContent(RpgItemStack rpgItemStack);
}
