package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;

public class RpgItemTypeImpl implements RpgItemType {

    protected WeaponClass weaponClass;
    protected double damage,armor;
    protected String id, modelName;

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
