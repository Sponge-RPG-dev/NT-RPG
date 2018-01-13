package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
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
public class ItemRarityData extends AbstractData<ItemRarityData, ItemRarityData.Immutable> {

    private Text rarity;

    public ItemRarityData() {
        this(Text.EMPTY);
    }

    public ItemRarityData(Text rarity) {
        this.rarity = rarity;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_RARITY, this::rarity);

        registerFieldGetter(NKeys.ITEM_RARITY, this::getRarity);

        registerFieldSetter(NKeys.ITEM_RARITY, this::setRarity);
    }

    public Value<Text> rarity() {
        return Sponge.getRegistry().getValueFactory()
                .createValue(NKeys.ITEM_RARITY, this.rarity);
    }

    public Text getRarity() {
        return rarity;
    }

    private void setRarity(Text rarity) {
        this.rarity = rarity;
    }

    @Override
    public Optional<ItemRarityData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemRarityData> a = dataHolder.get(ItemRarityData.class);
        if (a.isPresent()) {
            ItemRarityData otherData = a.get();
            ItemRarityData finalData = overlap.merge(this, otherData);
            this.rarity = finalData.rarity;
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

    public class Immutable extends AbstractImmutableData<ItemRarityData.Immutable, ItemRarityData> {

        private Text rarity;


        public Immutable(Text rarity) {
            this.rarity = rarity;
            registerGetters();
        }


        public Immutable() {
            this(Text.EMPTY);
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_RARITY, this::rarity);

            registerFieldGetter(NKeys.ITEM_RARITY, this::getrarity);
        }

        public ImmutableValue<Text> rarity() {
            return Sponge.getRegistry().getValueFactory()
                    .createValue(NKeys.ITEM_RARITY, this.rarity)
                    .asImmutable();
        }


        private Text getrarity() {
            return rarity;
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
        @SuppressWarnings("unchecked")
        protected Optional<ItemRarityData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_RARITY)) {
                return Optional.of(
                        new ItemRarityData((Text) container.get(NKeys.ITEM_RARITY.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }
            return Optional.empty();
        }
    }

}
