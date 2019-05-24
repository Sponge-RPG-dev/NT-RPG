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

import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemRarityData extends AbstractSingleData<Integer, ItemRarityData, ItemRarityData.Immutable> {

    public ItemRarityData() {
        this(0);
    }

    public ItemRarityData(Integer rarity) {
        super(rarity, NKeys.ITEM_RARITY);
        registerGettersAndSetters();
    }

    @Override
    public Optional<ItemRarityData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemRarityData> a = dataHolder.get(ItemRarityData.class);
        if (a.isPresent()) {
            ItemRarityData otherData = a.get();
            ItemRarityData finalData = overlap.merge(this, otherData);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemRarityData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemRarityData> from(DataView view) {
        if (view.contains(NKeys.ITEM_RARITY.getQuery())) {
            setValue((Integer) view.get(NKeys.ITEM_RARITY.getQuery()).get());
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ItemRarityData copy() {
        return new ItemRarityData(getValue());
    }

    @Override
    protected Value<Integer> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_RARITY, getValue());
    }

    @Override
    public ItemRarityData.Immutable asImmutable() {
        return new ItemRarityData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return ItemRarityData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_RARITY, getValue());
        return dataContainer;
    }

    public static class Builder extends AbstractDataBuilder<ItemRarityData>
            implements DataManipulatorBuilder<ItemRarityData, ItemRarityData.Immutable> {

        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemRarityData.class, CONTENT_VERSION);
        }

        @Override
        public ItemRarityData create() {
            return new ItemRarityData();
        }

        @Override
        public Optional<ItemRarityData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemRarityData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }

    public class Immutable extends AbstractImmutableSingleData<Integer, Immutable, ItemRarityData> {


        public Immutable(Integer rarity) {
            super(rarity, NKeys.ITEM_RARITY);
            registerGetters();
        }

        public Immutable() {
            this(0);
        }

        @Override
        public int getContentVersion() {
            return ItemRarityData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_RARITY, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<Integer> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_RARITY, getValue()).asImmutable();
        }

        @Override
        public ItemRarityData asMutable() {
            return new ItemRarityData(getValue());
        }
    }

}
