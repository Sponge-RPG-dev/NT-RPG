package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.Pair;
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
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemRarityData extends AbstractSingleData<Text, ItemRarityData, ItemRarityData.Immutable> {

    private Text rarity;

    public ItemRarityData() {
        this(Text.EMPTY);
    }

    public ItemRarityData(Text rarity) {
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
        if (!container.contains(NKeys.ITEM_RARITY)) {
            return Optional.empty();
        }

        rarity = (Text) container.getMap(NKeys.ITEM_RARITY.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public ItemRarityData copy() {
        return new ItemRarityData(rarity);
    }

    @Override
    protected Value<Text> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_RARITY, getValue());
    }

    @Override
    public ItemRarityData.Immutable asImmutable() {
        return new ItemRarityData.Immutable(rarity);
    }

    @Override
    public int getContentVersion() {
        return ItemRarityData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_RARITY, rarity);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableSingleData<Text, Immutable, ItemRarityData> {


        public Immutable(Text rarity) {
            super(rarity, NKeys.ITEM_RARITY);
            registerGetters();
        }

        public Immutable() {
            this(Text.EMPTY);
        }

        @Override
        public int getContentVersion() {
            return ItemRarityData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_RARITY, rarity);
            return dataContainer;
        }

        @Override
        protected ImmutableValue<Text> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_RARITY, getValue()).asImmutable();
        }

        @Override
        public ItemRarityData asMutable() {
            return new ItemRarityData(rarity);
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemRarityData> implements DataManipulatorBuilder<ItemRarityData, ItemRarityData.Immutable> {
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
            if (container.contains(NKeys.ITEM_RARITY)) {
                return Optional.of(
                        new ItemRarityData((Text) container.get(NKeys.ITEM_RARITY.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }

            if (container.contains(NKeys.ITEM_RARITY)) {

                ItemRarityData data = new ItemRarityData();
                Text t = (Text) container.get(NKeys.ITEM_RARITY.getQuery()).get();

                data.setValue(t);

                container.getSerializable(NKeys.ITEM_RARITY.getQuery(), ItemRarityData.class)
                .ifPresent(a -> {
                    data.set(NKeys.ITEM_RARITY, a.getValue());
                });


                return Optional.of(data);
            }

            return Optional.empty();
        }
    }

}
