package cz.neumimto.rpg.inventory.data.manipulators;

import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.sockets.SocketType;
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
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 13.1.2018.
 */
public class ItemSocketsData extends AbstractData<ItemSocketsData, ItemSocketsData.Immutable> {

    private List<SocketType> sockets;
    private List<Text> content;

    public ItemSocketsData() {
        this(new ArrayList<>(), new ArrayList<>());
        registerGettersAndSetters();
    }


    public ItemSocketsData(List<SocketType> any, List<Text> parse) {
        this.sockets = any;
        this.content = parse;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NKeys.ITEM_SOCKET_CONTAINER, this::container);
        registerFieldGetter(NKeys.ITEM_SOCKET_CONTAINER, this::getSockets);
        registerFieldSetter(NKeys.ITEM_SOCKET_CONTAINER, this::setSockets);

        registerKeyValue(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, this::content);
        registerFieldGetter(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, this::getContent);
        registerFieldSetter(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, this::setContent);
    }


    public List<SocketType> getSockets() {
        return sockets;
    }

    public void setSockets(List<SocketType> sockets) {
        this.sockets = sockets;
    }


    public ListValue<SocketType> container() {
        return Sponge.getRegistry().getValueFactory()
                .createListValue(NKeys.ITEM_SOCKET_CONTAINER, getSockets());
    }

    private ListValue<Text> content() {
        return Sponge.getRegistry().getValueFactory().createListValue(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, getContent());
    }

    public List<Text> getContent() {
        return content;
    }

    public void setContent(List<Text> content) {
        this.content = content;
    }

    @Override
    public Optional<ItemSocketsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<ItemSocketsData> a = dataHolder.get(ItemSocketsData.class);
        if (a.isPresent()) {
            ItemSocketsData otherData = a.get();
            ItemSocketsData finalData = overlap.merge(this, otherData);
            this.content = finalData.content;
            this.sockets = finalData.sockets;
        }
        return Optional.of(this);
    }

    @Override
    public Optional<ItemSocketsData> from(DataContainer container) {
        return from((DataView) container);
    }

    @SuppressWarnings("unchecked")
    public Optional<ItemSocketsData> from(DataView view) {
        if (view.contains(NKeys.ITEM_SOCKET_CONTAINER.getQuery())
                && view.contains(NKeys.ITEM_SOCKET_CONTAINER_CONTENT.getQuery())) {
            this.sockets = ((List<String>) view.getList(NKeys.ITEM_SOCKET_CONTAINER.getQuery()).get())
                    .stream()
                    .map(a -> {
                        SocketType type = Sponge.getRegistry().getType(SocketType.class, a).orElse(null);
                        return type;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            this.content = view.getSerializableList(NKeys.ITEM_SOCKET_CONTAINER_CONTENT.getQuery(), Text.class).get();
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ItemSocketsData copy() {
        return new ItemSocketsData(sockets, content);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(sockets, content);
    }

    @Override
    public int getContentVersion() {
        return Builder.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NKeys.ITEM_SOCKET_CONTAINER.getQuery(),
                        sockets.stream().map(SocketType::getId).collect(Collectors.toList()))
                .set(NKeys.ITEM_SOCKET_CONTAINER_CONTENT.getQuery(), content);
    }

    public static class Builder extends AbstractDataBuilder<ItemSocketsData> implements DataManipulatorBuilder<ItemSocketsData, Immutable> {

        public static int CONTENT_VERSION = 1;

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
        protected Optional<ItemSocketsData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }
    }

    public class Immutable extends AbstractImmutableData<Immutable, ItemSocketsData> {

        private List<SocketType> sockets;
        private List<Text> content;

        public Immutable(List<SocketType> sockets, List<Text> content) {
            this.sockets = sockets;
            this.content = content;
            registerGetters();
        }

        @Override
        public ItemSocketsData asMutable() {
            return new ItemSocketsData(sockets, content);
        }

        @Override
        public int getContentVersion() {
            return Builder.CONTENT_VERSION;
        }

        @Override
        protected void registerGetters() {
            registerKeyValue(NKeys.ITEM_SOCKET_CONTAINER, this::container);
            registerFieldGetter(NKeys.ITEM_SOCKET_CONTAINER, this::getSockets);

            registerKeyValue(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, this::content);
            registerFieldGetter(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, this::getContent);
        }


        public ImmutableListValue<SocketType> container() {
            return Sponge.getRegistry().getValueFactory()
                    .createListValue(NKeys.ITEM_SOCKET_CONTAINER, getSockets()).asImmutable();
        }

        private ImmutableListValue<Text> content() {
            return Sponge.getRegistry().getValueFactory()
                    .createListValue(NKeys.ITEM_SOCKET_CONTAINER_CONTENT, getContent()).asImmutable();
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer()
                    .set(NKeys.ITEM_SOCKET_CONTAINER.getQuery(),
                            sockets.stream().map(SocketType::getId).collect(java.util.stream.Collectors.toList()))
                    .set(NKeys.ITEM_SOCKET_CONTAINER_CONTENT.getQuery(), ItemSocketsData.this.getContent());
        }

        public List<SocketType> getSockets() {
            return sockets;
        }

        public List<Text> getContent() {
            return content;
        }


    }
}
