package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;

public class RpgItemTypeImpl implements RpgItemType {

    protected WeaponClass weaponClass;
    protected double damage,armor;
    protected String id, modelName;

    public RpgItemTypeImpl(String id, String modelName, WeaponClass weaponClass, double damage, double armor) {
        this.weaponClass = weaponClass;
        this.damage = damage;
        this.armor = armor;
        this.id = id;
        this.modelName = modelName;
    }

    @Override
    public WeaponClass getWeaponClass() {
        return weaponClass;
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

}
