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

/**
 * Created by NeumiMTo on 13.11.2016.
 */
public class InventoryCommandItemMenuData extends AbstractSingleData<String, InventoryCommandItemMenuData, InventoryCommandItemMenuData.Immutable> {

    public InventoryCommandItemMenuData(String value) {
        super(value, NKeys.COMMAND);
    }

    @Override
    public Optional<InventoryCommandItemMenuData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<InventoryCommandItemMenuData> otherData_ = dataHolder.get(InventoryCommandItemMenuData.class);
        if (otherData_.isPresent()) {
            InventoryCommandItemMenuData otherData = otherData_.get();
            InventoryCommandItemMenuData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<InventoryCommandItemMenuData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<InventoryCommandItemMenuData> from(DataView view) {
        if (view.contains(NKeys.COMMAND.getQuery())) {
            setValue(view.getString(NKeys.COMMAND.getQuery()).get());
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public InventoryCommandItemMenuData copy() {
        return new InventoryCommandItemMenuData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.COMMAND, getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new InventoryCommandItemMenuData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.COMMAND.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<String, Immutable, InventoryCommandItemMenuData> {


        public Immutable(String value) {
            super(value, NKeys.COMMAND);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.COMMAND, getValue()).asImmutable();
        }

        @Override
        public InventoryCommandItemMenuData asMutable() {
            return new InventoryCommandItemMenuData(getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.COMMAND.getQuery(), getValue());
        }
    }

    public static class InventoryCommandItemMenuDataBuilder extends AbstractDataBuilder<InventoryCommandItemMenuData>
            implements DataManipulatorBuilder<InventoryCommandItemMenuData, Immutable> {

        public InventoryCommandItemMenuDataBuilder() {
            super(InventoryCommandItemMenuData.class, 1);
        }

        @Override
        public InventoryCommandItemMenuData create() {
            return new InventoryCommandItemMenuData("");
        }

        @Override
        public Optional<InventoryCommandItemMenuData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<InventoryCommandItemMenuData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
