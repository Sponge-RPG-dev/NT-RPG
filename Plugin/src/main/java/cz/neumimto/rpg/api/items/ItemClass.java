package cz.neumimto.rpg.api.items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 29.4.2018.
 */
public class ItemClass {

	public static final ItemClass ARMOR = new ItemClass("Armor");

	public static final ItemClass SHIELD = new ItemClass("Shield");

	public static final ItemClass ACCESSORY = new ItemClass("Accessory");

	static {
		SHIELD.parent = ARMOR;
		ACCESSORY.items = Collections.emptySet();
		ACCESSORY.properties = Collections.emptySet();
		ACCESSORY.propertiesMults = Collections.emptySet();
		ACCESSORY.subClass = Collections.emptySet();
	}

	private final String name;

	private Set<ItemClass> subClass = new HashSet<>();

	private Set<RpgItemType> items = new HashSet<>();

	private ItemClass parent;

	private Set<Integer> properties = new HashSet<>();

	private Set<Integer> propertiesMults = new HashSet<>();

	public ItemClass(String name) {
		this.name = name;
	}

	public Set<ItemClass> getSubClass() {
		return subClass;
	}

	public void setSubClass(Set<ItemClass> subClass) {
		this.subClass = subClass;
	}

	public Set<RpgItemType> getItems() {
		return items;
	}

	public void setItems(Set<RpgItemType> items) {
		this.items = items;
	}

	public Set<Integer> getProperties() {
		return properties;
	}

	public void setProperties(Set<Integer> properties) {
		this.properties = properties;
	}

	public ItemClass getParent() {
		return parent;
	}

	public void setParent(ItemClass parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public Set<Integer> getPropertiesMults() {
		return propertiesMults;
	}

	@Override
	public String toString() {
		return "ItemClass{" +
				"name='" + name + '\'' +
				'}';
	}
}
