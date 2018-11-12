package cz.neumimto.rpg.inventory.items;

import org.spongepowered.api.CatalogType;

/**
 * Created by NeumimTo on 30.3.2018.
 */
public class ItemMetaType implements CatalogType {

	private final String name;
	private final String id;

	public ItemMetaType(String name) {
		this.id = "nt-rpg:" + name.toLowerCase();
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
}
