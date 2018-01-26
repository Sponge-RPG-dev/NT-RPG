package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
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

import java.sql.BatchUpdateException;
import java.util.Map;
import java.util.Optional;

public class ItemStackUpgradeData extends AbstractSingleData<ItemUpgrade, ItemStackUpgradeData, ItemStackUpgradeData.Immutable> {

    
    public ItemStackUpgradeData(ItemUpgrade value) {
        super(value, NKeys.ITEMSTACK_UPGRADE);
    }

    @Override
    protected Value<ItemUpgrade> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEMSTACK_UPGRADE, getValue());
    }


    @Override
    public Optional<ItemStackUpgradeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemStackUpgradeData> a = dataHolder.get(ItemStackUpgradeData.class);
        if (a.isPresent()) {
           ItemStackUpgradeData otherData = a.get();
           ItemStackUpgradeData finalData = overlap.merge(this, otherData);
            setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemStackUpgradeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemStackUpgradeData> from(DataView container) {
        if (!container.contains(NKeys.ITEMSTACK_UPGRADE)) {
            return Optional.empty();
        }
        Map<?, ?> map = container.getMap(NKeys.ITEMSTACK_UPGRADE.getQuery()).get();
        setValue(new ItemUpgrade((Map<? extends String, ?>) map));
        return Optional.of(this);
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEMSTACK_UPGRADE, getValue());
        return dataContainer;
    }

    @Override
    public ItemStackUpgradeData copy() {
        return new ItemStackUpgradeData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    public static class Immutable extends AbstractImmutableSingleData<ItemUpgrade, Immutable, ItemStackUpgradeData> {


        public Immutable(ItemUpgrade value) {
            super(value, NKeys.ITEMSTACK_UPGRADE);
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEMSTACK_UPGRADE, getValue());
            return dataContainer;
        }

        @Override
        protected ImmutableValue<ItemUpgrade> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEMSTACK_UPGRADE, getValue()).asImmutable();
        }

        @Override
        public ItemStackUpgradeData asMutable() {
            return new ItemStackUpgradeData(getValue());
        }
    }


    public static class Builder extends AbstractDataBuilder<ItemStackUpgradeData> implements DataManipulatorBuilder<ItemStackUpgradeData, ItemStackUpgradeData.Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemStackUpgradeData.class, CONTENT_VERSION);
        }

        @Override
        public ItemStackUpgradeData create() {
            return new ItemStackUpgradeData(new ItemUpgrade());
        }

        @Override
        public Optional<ItemStackUpgradeData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemStackUpgradeData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
