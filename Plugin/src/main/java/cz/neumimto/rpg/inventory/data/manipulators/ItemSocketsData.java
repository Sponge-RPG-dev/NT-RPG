package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.SocketType;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableMappedData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractMappedData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.text.Text;

import java.util.*;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemSocketsData extends AbstractMappedData<SocketType, Text, ItemSocketsData, ItemSocketsData.Immutable> {


    public ItemSocketsData() {
        this(new HashMap<>());
    }

    public ItemSocketsData(Map<SocketType, Text> sockets) {
        super(sockets, NKeys.ITEM_SOCKETS);
    }


    @Override
    public Optional<Text> get(SocketType key) {
        return Optional.of(getValue().get(key));
    }

    @Override
    public Set<SocketType> getMapKeys() {
        return getValue().keySet();
    }

    @Override
    public ItemSocketsData put(SocketType key, Text value) {
        getValue().put(key, value);
        return this;
    }

    @Override
    public ItemSocketsData putAll(Map<? extends SocketType, ? extends Text> map) {
        getValue().putAll(map);
        return this;
    }

    @Override
    public ItemSocketsData remove(SocketType key) {
        if (getValue().containsKey(key))
            getValue().remove(key);
        return this;
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
        if (!container.contains(NKeys.ITEM_SOCKETS)) {
            return Optional.empty();
        }

        setValue((Map<SocketType, Text>) container.getMap(NKeys.ITEM_SOCKETS.getQuery()).get());
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


    public class Immutable extends AbstractImmutableMappedData<SocketType, Text, Immutable, ItemSocketsData> {

        public Immutable(Map<SocketType, Text> sockets) {
            super(sockets, NKeys.ITEM_SOCKETS);
        }


        public Immutable() {
            this(Collections.emptyMap());
        }

        @Override
        public int getContentVersion() {
            return ItemSocketsData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_SOCKETS,getValue());
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
            if (container.contains(NKeys.ITEM_SOCKETS)) {

                Map<SocketType, Text> map = (Map<SocketType, Text>) container.getMap(NKeys.ITEM_SOCKETS.getQuery()).get();
                ItemSocketsData socketsData = new ItemSocketsData(map);

                container.getSerializable(NKeys.ITEM_SOCKETS.getQuery(), ItemSocketsData.class)
                .ifPresent(a -> {
                    socketsData.set(NKeys.ITEM_SOCKETS, a.getValue());
                });

                return Optional.of(socketsData);
            }
            return Optional.empty();
        }
    }
}