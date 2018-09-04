package cz.neumimto.rpg.inventory.sockets;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(SocketTypes.class)
public class SocketType implements CatalogType {

	private final String name;
	private final String id;

	public SocketType(String name) {
		this.id = name.toLowerCase();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SocketType that = (SocketType) o;
		return getId().equals(that.getId());
	}
}
