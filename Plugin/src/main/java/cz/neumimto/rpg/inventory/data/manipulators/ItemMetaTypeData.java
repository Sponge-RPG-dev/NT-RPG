package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import cz.neumimto.rpg.inventory.items.ItemMetaTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleCatalogData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleCatalogData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * Created by NeumimTo on 30.3.2018.
 */
public class ItemMetaTypeData extends AbstractSingleCatalogData<ItemMetaType, ItemMetaTypeData, ItemMetaTypeData.Immutable> {


    public ItemMetaTypeData(ItemMetaType value) {
        super(value, NKeys.ITEM_META_TYPE);
        registerFieldGetter(NKeys.ITEM_META_TYPE, this::getValueGetter);
        registerFieldSetter(NKeys.ITEM_META_TYPE, this::setValue);
    }

    @Override
    public Optional<ItemMetaTypeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemMetaTypeData> a = dataHolder.get(ItemMetaTypeData.class);
        if (a.isPresent()) {
            ItemMetaTypeData otherData = a.get();
            ItemMetaTypeData finalData = overlap.merge(this, otherData);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemMetaTypeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemMetaTypeData> from(DataView container) {
        if (!container.contains(NKeys.ITEM_META_TYPE)) {
            return Optional.empty();
        }

        setValue((ItemMetaType) container.get(NKeys.ITEM_META_TYPE.getQuery()).get());
        return Optional.of(this);
    }

    @Override
    public ItemMetaTypeData copy() {
        return new ItemMetaTypeData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(NKeys.ITEM_META_TYPE.getQuery(), getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    public static class Immutable extends AbstractImmutableSingleCatalogData<ItemMetaType, ItemMetaTypeData.Immutable, ItemMetaTypeData> {


        public Immutable(ItemMetaType value) {
            super(value, ItemMetaTypes.CHARM, NKeys.ITEM_META_TYPE);
            registerFieldGetter(NKeys.ITEM_META_TYPE, this::getValueGetter);
        }

        @Override
        public int getContentVersion() {
            return ItemMetaTypeData.Builder.CONTENT_VERSION;
        }


        @Override
        public ItemMetaTypeData asMutable() {
            return new ItemMetaTypeData(getValue());
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_META_TYPE.getQuery(), getValue());
        }
    }


    public static class Builder extends AbstractDataBuilder<ItemMetaTypeData>
            implements DataManipulatorBuilder<ItemMetaTypeData, ItemMetaTypeData.Immutable> {

        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemMetaTypeData.class, CONTENT_VERSION);
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
