package cz.neumimto.rpg.inventory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 29.4.2018.
 */
public class WeaponClass {

    public Set<WeaponClass> subClass = new HashSet<>();

    public Set<RPGItemType> items = new HashSet<>();

    public Set<String> properties = new HashSet<>();

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
}
