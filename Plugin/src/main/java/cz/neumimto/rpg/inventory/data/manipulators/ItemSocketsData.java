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
import java.util.Collections;
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
    @SuppressWarnings("unchecked")
    public Optional<ItemSocketsData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_STACK_UPGRADE_CONTAINER)) {
            return Optional.empty();
        }

        setValue((List<ItemSocket>) container.getMap(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery()).get());
        return Optional.of(this);
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
        return Builder.CONTENT_VERSION;
    }


    public class Immutable extends AbstractImmutableListData<ItemSocket, Immutable, ItemSocketsData> {

        public Immutable(List<ItemSocket> sockets) {
            super(sockets, NKeys.ITEM_STACK_UPGRADE_CONTAINER);
        }


        public Immutable() {
            this(Collections.emptyList());
        }

        @Override
        public int getContentVersion() {
            return ItemSocketsData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_STACK_UPGRADE_CONTAINER,getValue());
            return dataContainer;
        }

        @Override
        public ItemSocketsData asMutable() {
            return new ItemSocketsData(getValue());
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemSocketsData> implements DataManipulatorBuilder<ItemSocketsData, ItemSocketsData.Immutable> {
        public static final int CONTENT_VERSION = 1;

        public Builder() {
            super(ItemSocketsData.class, CONTENT_VERSION);
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
        @SuppressWarnings("unchecked")
        protected Optional<ItemSocketsData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(NKeys.ITEM_STACK_UPGRADE_CONTAINER)) {

                List<ItemSocket> map = (List<ItemSocket>) container.getMap(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery()).get();
                ItemSocketsData socketsData = new ItemSocketsData(map);

                container.getSerializable(NKeys.ITEM_STACK_UPGRADE_CONTAINER.getQuery(), ItemSocketsData.class)
                .ifPresent(a -> {
                    socketsData.set(NKeys.ITEM_STACK_UPGRADE_CONTAINER, a.getValue());
                });

                return Optional.of(socketsData);
            }
            return Optional.empty();
        }
    }
}