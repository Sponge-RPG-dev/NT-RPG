package cz.neumimto.rpg.sponge.inventory.data.manipulators;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.items.sockets.SocketType;
import cz.neumimto.rpg.api.items.sockets.SocketTypes;
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


public class ItemStackUpgradeData extends AbstractSingleData<SocketType, ItemStackUpgradeData, ItemStackUpgradeData.Immutable> {

    public ItemStackUpgradeData(SocketType value) {
        super(value, NKeys.ITEMSTACK_UPGRADE);
    }

    @Override
    public Optional<ItemStackUpgradeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemStackUpgradeData> otherData_ = dataHolder.get(ItemStackUpgradeData.class);
        if (otherData_.isPresent()) {
            ItemStackUpgradeData otherData = otherData_.get();
            ItemStackUpgradeData finalData = overlap.merge(this, otherData);
            finalData.setValue(otherData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemStackUpgradeData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemStackUpgradeData> from(DataView view) {
        if (view.contains(NKeys.ITEMSTACK_UPGRADE.getQuery())) {
            String s = view.getString(NKeys.ITEMSTACK_UPGRADE.getQuery()).get();
            SocketType socketType =  ((SpongeItemService) Rpg.get().getItemService()).getSocketTypes().get(s);
            setValue(socketType);
            return Optional.of(this);
        }
        return Optional.empty();

    }

    @Override
    public ItemStackUpgradeData copy() {
        return new ItemStackUpgradeData(getValue());
    }

    @Override
    protected Value<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEMSTACK_UPGRADE, getValue());
    }

    @Override
    public ItemStackUpgradeData.Immutable asImmutable() {
        return new ItemStackUpgradeData.Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return ItemStackUpgradeData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_META_SUBTYPE.getQuery(), getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<SocketType, Immutable, ItemStackUpgradeData> {


        public Immutable(SocketType value) {
            super(value, NKeys.ITEMSTACK_UPGRADE);
        }

        @Override
        protected ImmutableValue<?> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(NKeys.ITEMSTACK_UPGRADE, getValue()).asImmutable();
        }

        @Override
        public ItemStackUpgradeData asMutable() {
            return new ItemStackUpgradeData(getValue());
        }

        @Override
        public int getContentVersion() {
            return ItemStackUpgradeData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_META_SUBTYPE.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemStackUpgradeData>
            implements DataManipulatorBuilder<ItemStackUpgradeData, ItemStackUpgradeData.Immutable> {

        protected static int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemStackUpgradeData.class, 1);
        }

        @Override
        public ItemStackUpgradeData create() {
            return new ItemStackUpgradeData(SocketTypes.ANY);
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
