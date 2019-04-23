package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.items.WeaponClass;

import java.util.function.Predicate;

public class FilteredManagedSlotImpl extends ManagedSlotImpl {
    private final Predicate<WeaponClass> filter;

    public FilteredManagedSlotImpl(int id, Predicate<WeaponClass> filter) {
        super(id);
        this.filter = filter;
    }

    @Override
    public Predicate<WeaponClass> getFilter() {
        return filter;
    }
}
