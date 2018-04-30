package cz.neumimto.rpg.inventory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 29.4.2018.
 */
public class WeaponClass {

    private final String name;

    private Set<WeaponClass> subClass = new HashSet<>();

    private Set<RPGItemType> items = new HashSet<>();

    private Set<String> properties = new HashSet<>();
    private WeaponClass parent;

    public WeaponClass(String name) {
        this.name = name;
    }

    public Set<WeaponClass> getSubClass() {
        return subClass;
    }

    public void setSubClass(Set<WeaponClass> subClass) {
        this.subClass = subClass;
    }

    public Set<RPGItemType> getItems() {
        return items;
    }

    public void setItems(Set<RPGItemType> items) {
        this.items = items;
    }

    public Set<String> getProperties() {
        return properties;
    }

    public void setProperties(Set<String> properties) {
        this.properties = properties;
    }

    public void setParent(WeaponClass parent) {
        this.parent = parent;
    }

    public WeaponClass getParent() {
        return parent;
    }
}
