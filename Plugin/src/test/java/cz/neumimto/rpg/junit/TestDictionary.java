package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;

import java.util.Collections;

public class TestDictionary {

    public static final WeaponClass WEAPON_CLASS_1 = new WeaponClass("weaponclass1") ;
    public static final RpgItemType ITEM_TYPE_WEAPON_1 = new RpgItemTypeImpl("weapon1", null, WEAPON_CLASS_1, 10, 0);

    public static final WeaponClass WEAPON_CLASS_2 = new WeaponClass("weaponclass2");
    public static final RpgItemType ITEM_TYPE_WEAPON_2 = new RpgItemTypeImpl("weapon2", null, WEAPON_CLASS_2, 11, 0);

    public static final RpgItemType ARMOR_TYPE_1 = new RpgItemTypeImpl("armor1", null, WeaponClass.ARMOR, 0, 100);

    public static final Attribute STR = new Attribute("str", "str", 100, Collections.emptyMap(), null, null);
    public static final Attribute AGI = new Attribute("agi", "agi", 100, Collections.emptyMap(), null, null);


    public static final ClassDefinition CLASS_PRIMARY = new ClassDefinition("primary","Primary");

    public static final ClassDefinition CLASS_TERTIARY = new ClassDefinition("tertiary","Tertiary");

    public static final ClassDefinition CLASS_SECONDARY = new ClassDefinition("secondary","Secondary");


    static {
        WEAPON_CLASS_1.getItems().add(ITEM_TYPE_WEAPON_1);
        WEAPON_CLASS_2.getItems().add(ITEM_TYPE_WEAPON_2);

        WeaponClass.ARMOR.getItems().add(ARMOR_TYPE_1);

    }
}
