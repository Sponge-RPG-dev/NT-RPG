package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.sponge.inventory.LoreDurability;
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

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class LoreDurabilityData extends AbstractSingleData<LoreDurability, LoreDurabilityData, LoreDurabilityData.Immutable> {

    public LoreDurabilityData(int min, int max) {
        this(new LoreDurability(min, max));
    }


    public LoreDurabilityData() {
        this(0, 0);
    }

    public LoreDurabilityData(LoreDurability durability) {
        super(durability, NKeys.ITEM_LORE_DURABILITY);
    }

    @Override
    public Optional<LoreDurabilityData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<LoreDurabilityData> a = dataHolder.get(LoreDurabilityData.class);
        if (a.isPresent()) {
            LoreDurabilityData otherData = a.get();
            LoreDurabilityData finalData = overlap.merge(this, otherData);
            this.setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<LoreDurabilityData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_LORE_DURABILITY)) {
            return Optional.empty();
        }

        setValue((LoreDurability) container.get(NKeys.ITEM_LORE_DURABILITY.getQuery()).get());
        return Optional.of(this);
    }

    @Override
    public LoreDurabilityData copy() {
        return new LoreDurabilityData(getValue());
    }

    @Override
    protected Value<LoreDurability> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_LORE_DURABILITY, getValue());
    }

    @Override
    public LoreDurabilityData.Immutable asImmutable() {
        return new LoreDurabilityData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return LoreDurabilityData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_LORE_DURABILITY, getValue());
        return dataContainer;
    }

    public static class Builder extends AbstractDataBuilder<LoreDurabilityData> implements DataManipulatorBuilder<LoreDurabilityData, Immutable> {

        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(LoreDurabilityData.class, CONTENT_VERSION);
        }

        @Override
        public LoreDurabilityData create() {
            return new LoreDurabilityData();
        }

        @Override
        public Optional<LoreDurabilityData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<LoreDurabilityData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_LORE_DURABILITY)) {

                LoreDurabilityData data = new LoreDurabilityData();
                LoreDurability t = (LoreDurability) container.get(NKeys.ITEM_LORE_DURABILITY.getQuery()).get();
                data.setValue(t);
                container.getSerializable(NKeys.ITEM_LORE_DURABILITY.getQuery(), LoreDurabilityData.class)
                        .ifPresent(a -> {
                            data.set(NKeys.ITEM_LORE_DURABILITY, a.getValue());
                        });


                return Optional.of(data);
            }
            return Optional.empty();
        }
    }

    public class Immutable extends AbstractImmutableSingleData<LoreDurability, Immutable, LoreDurabilityData> {

        public Immutable(LoreDurability durability) {
            super(durability, NKeys.ITEM_LORE_DURABILITY);
        }


        public Immutable() {
            this(new LoreDurability(0, 0));
        }


        @Override
        public int getContentVersion() {
            return LoreDurabilityData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_LORE_DURABILITY, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_LORE_DURABILITY, getValue()).asImmutable();
        }

        @Override
        public LoreDurabilityData asMutable() {
            return new LoreDurabilityData(getValue());
        }
    }

}
