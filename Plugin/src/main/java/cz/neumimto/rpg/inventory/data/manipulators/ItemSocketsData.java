package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.ItemSocket;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableListData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractListData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemSocketsData extends AbstractListData<ItemSocket, ItemSocketsData, ItemSocketsData.Immutable> {


    public ItemSocketsData() {
        this(new ArrayList<>());
    }

    public ItemSocketsData(List<ItemSocket> sockets) {
        super(sockets, NKeys.ITEM_STACK_UPGRADE_CONTAINER);
    }


    @Override
    public Optional<ItemSocketsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemSocketsData> a = dataHolder.get(ItemSocketsData.class);
        if (a.isPresent()) {
            ItemSocketsData otherData = a.get();
            ItemSocketsData finalData = overlap.merge(this, otherData);
            this.setValue(finalData.getValue());
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemSocketsData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<ItemSocketsData> from(DataView view) {
        if (view.contains(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery())) {
            setValue((List<ItemSocket>) view.getList(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery()).get());
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ItemSocketsData copy() {
        return new ItemSocketsData(getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery(), getValue());
    }


    public class Immutable extends AbstractImmutableListData<ItemSocket, Immutable, ItemSocketsData> {


        public Immutable(List<ItemSocket> value) {
            super(value, NKeys.ITEM_STACK_UPGRADE_CONTAINER);
        }

        @Override
        public ItemSocketsData asMutable() {
            return new ItemSocketsData(getValue());
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery(), getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemSocketsData> implements DataManipulatorBuilder<ItemSocketsData, Immutable> {
        public Builder() {
            super(ItemSocketsData.class, 1);
        }

        @Override
        public ItemSocketsData create() {
            return new ItemSocketsData();
        }

        @Override
        public Optional<ItemSocketsData> createFrom(DataHolder dataHolder) {

            return create().fill(dataHolder);
        }

        @Override
        protected Optional<ItemSocketsData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }
}
