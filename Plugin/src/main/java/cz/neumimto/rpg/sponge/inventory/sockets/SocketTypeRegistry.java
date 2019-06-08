package cz.neumimto.rpg.sponge.inventory.sockets;

import com.google.common.collect.Maps;
import cz.neumimto.rpg.common.inventory.sockets.SocketType;
import cz.neumimto.rpg.sponge.inventory.items.subtypes.ItemSubtypeWrapper;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class SocketTypeRegistry implements AdditionalCatalogRegistryModule<SocketTypeWrapper> {

    @RegisterCatalog(SocketTypeWrapper.class)
    private final Map<String, SocketTypeWrapper> socketTypes = Maps.newHashMap();


    @Override
    public void registerAdditionalCatalog(SocketTypeWrapper extraCatalog) {
        checkArgument(!socketTypes.containsKey(extraCatalog.getId()));
        socketTypes.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<SocketTypeWrapper> getById(String id) {
        return Optional.of(socketTypes.get(id.toLowerCase()));
    }

    @Override
    public Collection<SocketTypeWrapper> getAll() {
        return socketTypes.values();
    }
}
