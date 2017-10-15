package cz.neumimto.rpg.inventory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 6.10.2017.
 */
public class ItemGroup {
    private Set<RPGItemType> itemTypes = new HashSet<>();
    private int damageMultPropertyId;
    private final String groupName;

    public ItemGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<RPGItemType> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(Set<RPGItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public int getDamageMultPropertyId() {
        return damageMultPropertyId;
    }

    public void setDamageMultPropertyId(int damageMultPropertyId) {
        this.damageMultPropertyId = damageMultPropertyId;
    }
}
