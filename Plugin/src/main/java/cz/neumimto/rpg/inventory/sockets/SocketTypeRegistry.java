package cz.neumimto.rpg.inventory.sockets;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Maps;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class SocketTypeRegistry implements AdditionalCatalogRegistryModule<SocketType> {

	@RegisterCatalog(SocketType.class)
	private final Map<String, SocketType> socketTypes = Maps.newHashMap();


	@Override
	public void registerAdditionalCatalog(SocketType extraCatalog) {
		checkArgument(!socketTypes.containsKey(extraCatalog.getId()));
		socketTypes.put(extraCatalog.getId(), extraCatalog);
	}

	@Override
	public Optional<SocketType> getById(String id) {
		return Optional.of(socketTypes.get(id.toLowerCase()));
	}

	@Override
	public Collection<SocketType> getAll() {
		return socketTypes.values();
	}
}
