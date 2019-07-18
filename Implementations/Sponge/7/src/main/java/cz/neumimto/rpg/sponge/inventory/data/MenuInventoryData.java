package cz.neumimto.rpg.sponge.inventory.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableBooleanData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractBooleanData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class MenuInventoryData extends AbstractBooleanData<MenuInventoryData, MenuInventoryData.Immutable> {

    public MenuInventoryData(Boolean b) {
        super(b, NKeys.MENU_INVENTORY, true);
    }

    @Override
    protected Value<Boolean> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.MENU_INVENTORY, getValue());
    }

    @Override
    public Optional<MenuInventoryData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<MenuInventoryData> data_ = dataHolder.get(MenuInventoryData.class);
        if (data_.isPresent()) {
            MenuInventoryData data = data_.get();
            MenuInventoryData finalData = overlap.merge(this, data);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }


    @Override
    public Optional<MenuInventoryData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<MenuInventoryData> from(DataView view) {
        if (view.contains(NKeys.MENU_INVENTORY.getQuery())) {
            setValue(view.getBoolean(NKeys.MENU_INVENTORY.getQuery()).get());
            return Optional.of(this);
        }
        return Optional.empty();

    }


    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(NKeys.MENU_INVENTORY, getValue());
    }

    @Override
    public MenuInventoryData copy() {
        return new MenuInventoryData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public static class Immutable extends AbstractImmutableBooleanData<Immutable, MenuInventoryData> {

        public Immutable(Boolean b) {
            super(b, NKeys.MENU_INVENTORY, true);
        }

        @Override
        public MenuInventoryData asMutable() {
            return new MenuInventoryData(getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }
    }

    public static class Builder extends AbstractDataBuilder<MenuInventoryData> implements DataManipulatorBuilder<MenuInventoryData, Immutable> {

        public Builder() {
            super(MenuInventoryData.class, 1);
        }

        @Override
        public MenuInventoryData create() {
            return new MenuInventoryData(true);
        }

        @Override
        public Optional<MenuInventoryData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<MenuInventoryData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}