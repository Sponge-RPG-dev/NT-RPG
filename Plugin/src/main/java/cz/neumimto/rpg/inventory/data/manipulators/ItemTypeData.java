package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.NKeys;
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
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by NeumimTo on 28.1.2018.
 */
public class ItemTypeData extends AbstractSingleData<Text, ItemTypeData, ItemTypeData.Immutable> {

    public ItemTypeData() {
        this(Text.EMPTY);
    }

    public ItemTypeData(Text rarity) {
        super(rarity, NKeys.ITEM_META_HEADER);
        registerGettersAndSetters();
    }

    @Override
    public Optional<ItemTypeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemTypeData> a = dataHolder.get(ItemTypeData.class);
        if (a.isPresent()) {
            ItemTypeData otherData = a.get();
            ItemTypeData finalData = overlap.merge(this, otherData);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }


    @Override
    public Optional<ItemTypeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemTypeData> from(DataView view) {
        if (view.contains(NKeys.ITEM_META_HEADER.getQuery())) {
            setValue((Text) view.get(NKeys.ITEM_META_HEADER.getQuery()).get());
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ItemTypeData copy() {
        return new ItemTypeData(getValue());
    }

    @Override
    protected Value<Text> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_HEADER, getValue());
    }

    @Override
    public ItemTypeData.Immutable asImmutable() {
        return new ItemTypeData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return ItemTypeData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_META_HEADER, getValue());
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableSingleData<Text, Immutable, ItemTypeData> {


        public Immutable(Text rarity) {
            super(rarity, NKeys.ITEM_META_HEADER);
            registerGetters();
        }

        public Immutable() {
            this(Text.EMPTY);
        }

        @Override
        public int getContentVersion() {
            return ItemTypeData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_META_HEADER, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<Text> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_META_HEADER, getValue()).asImmutable();
        }

        @Override
        public ItemTypeData asMutable() {
            return new ItemTypeData(getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemTypeData> implements DataManipulatorBuilder<ItemTypeData, Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemTypeData.class, CONTENT_VERSION);
        }

        @Override
        public ItemTypeData create() {
            return new ItemTypeData();
        }

        @Override
        public Optional<ItemTypeData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemTypeData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }

}
