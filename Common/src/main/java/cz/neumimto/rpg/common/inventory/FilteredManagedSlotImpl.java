package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.items.RpgItemStack;

import java.util.function.Predicate;

public class FilteredManagedSlotImpl extends ManagedSlotImpl {
    private final Predicate<RpgItemStack> filter;

    public FilteredManagedSlotImpl(int id, Predicate<RpgItemStack> filter) {
        super(id);
        this.filter = filter;
    }

    @Override
    public Predicate<RpgItemStack> getFilter() {
        return filter;
    }
}
