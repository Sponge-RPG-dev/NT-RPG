package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.sponge.inventory.ItemDamage;
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
public class LoreDamageData extends AbstractSingleData<ItemDamage, LoreDamageData, LoreDamageData.Immutable> {


    public LoreDamageData(double min, double max) {
        this(new ItemDamage(min, max));
    }


    public LoreDamageData() {
        this(0D, 0D);
    }

    public LoreDamageData(ItemDamage damage) {
        super(damage, NKeys.ITEM_DAMAGE);
    }

    @SuppressWarnings("unchecked")
    public ItemDamage getDamage() {
        return getValue();
    }


    @Override
    public Optional<LoreDamageData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<LoreDamageData> a = dataHolder.get(LoreDamageData.class);
        if (a.isPresent()) {
            LoreDamageData otherData = a.get();
            LoreDamageData finalData = overlap.merge(this, otherData);
            this.setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<LoreDamageData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_DAMAGE)) {
            return Optional.empty();
        }

        setValue((ItemDamage) container.get(NKeys.ITEM_DAMAGE.getQuery()).get());
        return Optional.of(this);
    }

    @Override
    public LoreDamageData copy() {
        return new LoreDamageData(getValue());
    }

    @Override
    protected Value<ItemDamage> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_DAMAGE, getValue());
    }

    @Override
    public LoreDamageData.Immutable asImmutable() {
        return new LoreDamageData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return LoreDamageData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_DAMAGE, getValue());
        return dataContainer;
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

                LoreDamageData data = new LoreDamageData();
                ItemDamage t = (ItemDamage) container.get(NKeys.ITEM_DAMAGE.getQuery()).get();
                data.setValue(t);
                container.getSerializable(NKeys.ITEM_DAMAGE.getQuery(), LoreDamageData.class)
                        .ifPresent(a -> {
                            data.set(NKeys.ITEM_DAMAGE, a.getValue());
                        });


                return Optional.of(data);
            }

            return Optional.empty();
        }
    }

    public class Immutable extends AbstractImmutableSingleData<ItemDamage, Immutable, LoreDamageData> {


        public Immutable(ItemDamage damage) {
            super(damage, NKeys.ITEM_DAMAGE);
        }


        public Immutable() {
            this(new ItemDamage(0D, 0D));
        }


        @Override
        public int getContentVersion() {
            return LoreDamageData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_DAMAGE, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEM_DAMAGE, getValue()).asImmutable();
        }

        @Override
        public LoreDamageData asMutable() {
            return new LoreDamageData(getValue());
        }
    }

}