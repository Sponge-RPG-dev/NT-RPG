package cz.neumimto.rpg.common.items;

public class RpgItemTypeImpl implements RpgItemType {

    protected ItemClass itemClass;
    protected double damage, armor;
    protected String id, modelName;
    protected String permission;

    public RpgItemTypeImpl(String id, String modelName, ItemClass itemClass, double damage, double armor, String permission) {
        this.itemClass = itemClass;
        this.damage = damage;
        this.armor = armor;
        this.id = id;
        this.modelName = modelName;
        this.permission = permission;
    }

    @Override
    public ItemClass getItemClass() {
        return itemClass;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public double getArmor() {
        return armor;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getModelId() {
        return modelName;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "RpgItemTypeImpl{" +
                "itemClass=" + itemClass +
                ", id='" + id + '\'' +
                '}';
    }
}
