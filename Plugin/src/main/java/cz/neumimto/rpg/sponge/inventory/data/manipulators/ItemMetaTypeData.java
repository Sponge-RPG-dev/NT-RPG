package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import cz.neumimto.rpg.sponge.inventory.items.ItemMetaType;
import cz.neumimto.rpg.sponge.inventory.items.ItemMetaTypes;
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
 * Created by NeumimTo on 30.3.2018.
 */
//public class ItemMetaTypeData extends AbstractSingleCatalogData<ItemMetaType, ItemMetaTypeData, ItemMetaTypeData.Immutable> {
//ITEM_META_TYPE
public class ItemMetaTypeData extends AbstractSingleData<ItemMetaType, ItemMetaTypeData, ItemMetaTypeData.Immutable> {

    public ItemMetaTypeData(ItemMetaType value) {
        super(value, NKeys.ITEM_META_TYPE);
    }

    @Override
    public Optional<ItemMetaTypeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemMetaTypeData> otherData_ = dataHolder.get(ItemMetaTypeData.class);
        if (otherData_.isPresent()) {
            ItemMetaTypeData otherData = otherData_.get();
            ItemMetaTypeData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemMetaTypeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemMetaTypeData> from(DataView view) {
        if (view.contains(NKeys.ITEM_META_TYPE.getQuery())) {
            setValue(Sponge.getRegistry().getType(ItemMetaType.class, view.getString(NKeys.ITEM_META_TYPE.getQuery()).get()).get());
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public ItemMetaTypeData copy() {
        return new ItemMetaTypeData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_TYPE, getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new ItemMetaTypeData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_META_TYPE.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<ItemMetaType, Immutable, ItemMetaTypeData> {


        public Immutable(ItemMetaType value) {
            super(value, NKeys.ITEM_META_TYPE);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_TYPE, getValue()).asImmutable();
        }

        @Override
        public ItemMetaTypeData asMutable() {
            return new ItemMetaTypeData(getValue());
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_META_TYPE.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemMetaTypeData>
            implements DataManipulatorBuilder<ItemMetaTypeData, Immutable> {

        protected static int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemMetaTypeData.class, 1);
        }

        @Override
        public ItemMetaTypeData create() {
            return new ItemMetaTypeData(ItemMetaTypes.CHARM);
        }

        @Override
        public Optional<ItemMetaTypeData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemMetaTypeData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
  