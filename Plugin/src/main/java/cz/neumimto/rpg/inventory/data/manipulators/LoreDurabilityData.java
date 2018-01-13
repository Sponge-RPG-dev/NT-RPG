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

import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class LoreDurabilityData extends AbstractData<LoreDurabilityData, LoreDurabilityData.Immutable> {

    private Pair<Integer, Integer> durability;


    public LoreDurabilityData(int min, int max) {
        this(new Pair<>(min, max));
    }


    public LoreDurabilityData() {
        this(0, 0);
    }

    public LoreDurabilityData(Pair<Integer, Integer> durability) {
        this.durability = durability;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_LORE_DURABILITY, this::durability);

        registerFieldGetter(NKeys.ITEM_LORE_DURABILITY, this::getDamage);

        registerFieldSetter(NKeys.ITEM_LORE_DURABILITY, this::setDamage);
    }

    public Value<Pair<Integer, Integer>> durability() {
        return Sponge.getRegistry().getValueFactory()
                .createValue(NKeys.ITEM_LORE_DURABILITY, this.durability);
    }

    public Pair<Integer, Integer> getDamage() {
        return durability;
    }

    private void setDamage(Pair<Integer, Integer> durability) {
        this.durability = durability;
    }

    @Override
    public Optional<LoreDurabilityData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<LoreDurabilityData> a = dataHolder.get(LoreDurabilityData.class);
        if (a.isPresent()) {
            LoreDurabilityData otherData = a.get();
            LoreDurabilityData finalData = overlap.merge(this, otherData);
            this.durability = finalData.durability;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<LoreDurabilityData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_LORE_DURABILITY)) {
            return Optional.empty();
        }

        durability = (Pair<Integer, Integer>) container.get(NKeys.ITEM_LORE_DURABILITY.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public LoreDurabilityData copy() {
        return new LoreDurabilityData(durability);
    }

    @Override
    public LoreDurabilityData.Immutable asImmutable() {
        return new LoreDurabilityData.Immutable(durability);
    }

    @Override
    public int getContentVersion() {
        return LoreDurabilityData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_LORE_DURABILITY, durability);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableData<Immutable, LoreDurabilityData> {

        private Pair<Integer, Integer> durability;


        public Immutable(Pair<Integer, Integer> durability) {
            this.durability = durability;
            registerGetters();
        }


        public Immutable() {
            this(new Pair<>(0,0));
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_LORE_DURABILITY, this::durability);

            registerFieldGetter(NKeys.ITEM_LORE_DURABILITY, this::getdurability);
        }

        public ImmutableValue<Pair<Integer, Integer>> durability() {
            return Sponge.getRegistry().getValueFactory()
                    .createValue(NKeys.ITEM_LORE_DURABILITY, this.durability)
                    .asImmutable();
        }


        private Pair<Integer, Integer> getdurability() {
            return durability;
        }

        @Override
        public int getContentVersion() {
            return LoreDurabilityData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_LORE_DURABILITY, durability);
            return dataContainer;
        }

        @Override
        public LoreDurabilityData asMutable() {
            return new LoreDurabilityData(durability);
        }
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
                return Optional.of(
                        new LoreDurabilityData((Pair<Integer, Integer>) container.get(NKeys.ITEM_LORE_DURABILITY.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }
            return Optional.empty();
        }
    }

}
