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
public class LoreDamageData extends AbstractData<LoreDamageData, LoreDamageData.Immutable> {

    private Pair<Double, Double> damage;


    public LoreDamageData(double min, double max) {
        this(new Pair<>(min, max));
    }


    public LoreDamageData() {
        this(0D, 0D);
    }

    public LoreDamageData(Pair<Double, Double> damage) {
        this.damage = damage;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_DAMAGE, this::damage);

        registerFieldGetter(NKeys.ITEM_DAMAGE, this::getDamage);

        registerFieldSetter(NKeys.ITEM_DAMAGE, this::setDamage);
    }

    public Value<Pair<Double, Double>> damage() {
        return Sponge.getRegistry().getValueFactory()
                .createValue(NKeys.ITEM_DAMAGE, this.damage);
    }

    public Pair<Double, Double> getDamage() {
        return damage;
    }

    private void setDamage(Pair<Double, Double> damage) {
        this.damage = damage;
    }

    @Override
    public Optional<LoreDamageData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<LoreDamageData> a = dataHolder.get(LoreDamageData.class);
        if (a.isPresent()) {
            LoreDamageData otherData = a.get();
            LoreDamageData finalData = overlap.merge(this, otherData);
            this.damage = finalData.damage;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<LoreDamageData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_DAMAGE)) {
            return Optional.empty();
        }

        damage = (Pair<Double, Double>) container.get(NKeys.ITEM_DAMAGE.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public LoreDamageData copy() {
        return new LoreDamageData(damage);
    }

    @Override
    public LoreDamageData.Immutable asImmutable() {
        return new LoreDamageData.Immutable(damage);
    }

    @Override
    public int getContentVersion() {
        return LoreDamageData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_DAMAGE, damage);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableData<Immutable, LoreDamageData> {

        private Pair<Double, Double> damage;


        public Immutable(Pair<Double, Double> damage) {
            this.damage = damage;
            registerGetters();
        }


        public Immutable() {
            this(new Pair<>(0D,0D));
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_DAMAGE, this::damage);

            registerFieldGetter(NKeys.ITEM_DAMAGE, this::getdamage);
        }

        public ImmutableValue<Pair<Double, Double>> damage() {
            return Sponge.getRegistry().getValueFactory()
                    .createValue(NKeys.ITEM_DAMAGE, this.damage)
                    .asImmutable();
        }


        private Pair<Double, Double> getdamage() {
            return damage;
        }

        @Override
        public int getContentVersion() {
            return LoreDamageData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_DAMAGE, damage);
            return dataContainer;
        }

        @Override
        public LoreDamageData asMutable() {
            return new LoreDamageData(damage);
        }
    }

    public static class Builder extends AbstractDataBuilder<LoreDamageData> implements DataManipulatorBuilder<LoreDamageData, Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(LoreDamageData.class, CONTENT_VERSION);
        }

        @Override
        public LoreDamageData create() {
            return new LoreDamageData();
        }

        @Override
        public Optional<LoreDamageData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Optional<LoreDamageData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_DAMAGE)) {
                return Optional.of(
                        new LoreDamageData((Pair<Double, Double>) container.get(NKeys.ITEM_DAMAGE.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }
            return Optional.empty();
        }
    }

}