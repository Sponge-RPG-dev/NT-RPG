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
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemSocketsData extends AbstractData<ItemSocketsData, ItemSocketsData.Immutable> {

    private Map<Text, Text> sockets;

    public ItemSocketsData() {
        this(Collections.emptyMap());
    }

    public ItemSocketsData(Map<Text, Text> sockets) {
        this.sockets = sockets;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_SOCKETS, this::sockets);

        registerFieldGetter(NKeys.ITEM_SOCKETS, this::getSockets);

        registerFieldSetter(NKeys.ITEM_SOCKETS, this::setSockets);
    }

    public MapValue<Text, Text> sockets() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(NKeys.ITEM_SOCKETS, this.sockets);
    }

    public Map<Text, Text> getSockets() {
        return sockets;
    }

    private void setSockets(Map<Text, Text> sockets) {
        this.sockets = sockets;
    }

    @Override
    public Optional<ItemSocketsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemSocketsData> a = dataHolder.get(ItemSocketsData.class);
        if (a.isPresent()) {
            ItemSocketsData otherData = a.get();
            ItemSocketsData finalData = overlap.merge(this, otherData);
            this.sockets = finalData.sockets;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemSocketsData> from(DataContainer container) {
        if (!container.contains(NKeys.ITEM_SOCKETS)) {
            return Optional.empty();
        }

        sockets = (Map<Text, Text>) container.getMap(NKeys.ITEM_SOCKETS.getQuery()).get();
        return Optional.of(this);
    }

    @Override
    public ItemSocketsData copy() {
        return new ItemSocketsData(sockets);
    }

    @Override
    public ItemSocketsData.Immutable asImmutable() {
        return new ItemSocketsData.Immutable(sockets);
    }

    @Override
    public int getContentVersion() {
        return ItemSocketsData.Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer.set(NKeys.ITEM_SOCKETS, sockets);
        return dataContainer;
    }

    public class Immutable extends AbstractImmutableData<Immutable, ItemSocketsData> {

        private Map<Text, Text> sockets;


        public Immutable(Map<Text, Text> sockets) {
            this.sockets = sockets;
            registerGetters();
        }


        public Immutable() {
            this(Collections.emptyMap());
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_SOCKETS, this::sockets);

            registerFieldGetter(NKeys.ITEM_SOCKETS, this::getsockets);
        }

        public ImmutableMapValue<Text, Text> sockets() {
            return Sponge.getRegistry().getValueFactory()
                    .createMapValue(NKeys.ITEM_SOCKETS, this.sockets)
                    .asImmutable();
        }


        private Map<Text, Text> getsockets() {
            return sockets;
        }

        @Override
        public int getContentVersion() {
            return ItemSocketsData.Builder.CONTENT_VERSION;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer dataContainer = super.toContainer();
            dataContainer.set(NKeys.ITEM_SOCKETS, sockets);
            return dataContainer;
        }

        @Override
        public ItemSocketsData asMutable() {
            return new ItemSocketsData(sockets);
        }
    }

    public static class Builder extends AbstractDataBuilder<ItemSocketsData> implements DataManipulatorBuilder<ItemSocketsData, Immutable> {
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
                return Optional.of(
                        new ItemSocketsData((Map<Text, Text>) container.get(NKeys.ITEM_SOCKETS.getQuery()).orElse(new Pair<>(0D,0D)))
                );
            }
            return Optional.empty();
        }
    }

}
