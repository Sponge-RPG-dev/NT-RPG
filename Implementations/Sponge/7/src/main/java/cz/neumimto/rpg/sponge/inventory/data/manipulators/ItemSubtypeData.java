package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.api.items.subtypes.ItemSubtypes;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
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

public class ItemSubtypeData extends AbstractSingleData<ItemSubtype, ItemSubtypeData, ItemSubtypeData.Immutable> {

    public ItemSubtypeData(ItemSubtype value) {
        super(value, NKeys.ITEM_META_SUBTYPE);
    }

    @Override
    public Optional<ItemSubtypeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemSubtypeData> otherData_ = dataHolder.get(ItemSubtypeData.class);
        if (otherData_.isPresent()) {
            ItemSubtypeData otherData = otherData_.get();
            ItemSubtypeData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemSubtypeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemSubtypeData> from(DataView view) {
        if (view.contains(NKeys.ITEM_META_SUBTYPE.getQuery())) {
            String s = view.getString(NKeys.ITEM_META_SUBTYPE.getQuery()).get();
            ItemSubtype itemSubtype =  ((SpongeItemService)Rpg.get().getItemService()).getItemSubtypes().get(s);
            setValue(itemSubtype);
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public ItemSubtypeData copy() {
        return new ItemSubtypeData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_SUBTYPE, getValue());
    }

    @Override
    public ItemSubtypeData.Immutable asImmutable() {
        return new ItemSubtypeData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return ItemSubtypeData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_META_SUBTYPE.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<ItemSubtype, ItemSubtypeData.Immutable, ItemSubtypeData> {


        public Immutable(ItemSubtype value) {
            super(value, NKeys.ITEM_META_SUBTYPE);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_SUBTYPE, getValue()).asImmutable();
        }

        @Override
        public ItemSubtypeData asMutable() {
            return new ItemSubtypeData(getValue());
        }

        @Override
        public int getContentVersion() {
            return ItemSubtypeData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_META_SUBTYPE.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemSubtypeData>
            implements DataManipulatorBuilder<ItemSubtypeData, ItemSubtypeData.Immutable> {

        protected static int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemSubtypeData.class, 1);
        }

        @Override
        public ItemSubtypeData create() {
            return new ItemSubtypeData(ItemSubtypes.ANY);
        }

        @Override
        public Optional<ItemSubtypeData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemSubtypeData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
