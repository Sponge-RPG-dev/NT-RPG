package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.api.items.ItemClass;

import java.util.function.Predicate;

public class FilteredManagedSlotImpl extends ManagedSlotImpl {
    private final Predicate<ItemClass> filter;

    public FilteredManagedSlotImpl(int id, Predicate<ItemClass> filter) {
        super(id);
        this.filter = filter;
    }

    @Override
    public Predicate<ItemClass> getFilter() {
        return filter;
    }
}
