package cz.neumimto.rpg.sponge.inventory.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class InventoryActionItemMenuData extends AbstractSingleData<Integer, InventoryActionItemMenuData, InventoryActionItemMenuData.Immutable> {

    public InventoryActionItemMenuData(Integer value) {
        super(value, NKeys.MENU_ACTION);
    }

    @Override
    public Optional<InventoryActionItemMenuData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<InventoryActionItemMenuData> otherData_ = dataHolder.get(InventoryActionItemMenuData.class);
        if (otherData_.isPresent()) {
            InventoryActionItemMenuData otherData = otherData_.get();
            InventoryActionItemMenuData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<InventoryActionItemMenuData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<InventoryActionItemMenuData> from(DataView view) {
        if (view.contains(NKeys.MENU_ACTION.getQuery())) {
            setValue(view.getInt(NKeys.MENU_ACTION.getQuery()).get());
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public InventoryActionItemMenuData copy() {
        return new InventoryActionItemMenuData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.MENU_ACTION, getValue());
    }

    @Override
    public InventoryActionItemMenuData.Immutable asImmutable() {
        return new InventoryActionItemMenuData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(NKeys.MENU_ACTION.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<Integer, InventoryActionItemMenuData.Immutable, InventoryActionItemMenuData> {

        public Immutable(Integer value) {
            super(value, NKeys.MENU_ACTION);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.MENU_ACTION, getValue()).asImmutable();
        }

        @Override
        public InventoryActionItemMenuData asMutable() {
            return new InventoryActionItemMenuData(getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.MENU_ACTION.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<InventoryActionItemMenuData>
            implements DataManipulatorBuilder<InventoryActionItemMenuData, InventoryActionItemMenuData.Immutable> {

        public Builder() {
            super(InventoryActionItemMenuData.class, 1);
        }

        @Override
        public InventoryActionItemMenuData create() {
            return new InventoryActionItemMenuData(-1);
        }

        @Override
        public Optional<InventoryActionItemMenuData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<InventoryActionItemMenuData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
