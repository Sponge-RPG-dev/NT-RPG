package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;

import java.util.HashMap;

public class TestDictionary {

    public static ItemClass WEAPON_CLASS_1;
    public static RpgItemType ITEM_TYPE_WEAPON_1;
    public static ItemClass WEAPON_CLASS_2;
    public static RpgItemType ITEM_TYPE_WEAPON_2;
    public static RpgItemType ARMOR_TYPE_1;

    public static AttributeConfig STR;
    public static AttributeConfig AGI;


    public static ClassDefinition CLASS_PRIMARY;
    public static ClassDefinition CLASS_TERTIARY;
    public static ClassDefinition CLASS_SECONDARY;


    public void reset() {
        WEAPON_CLASS_1 = new ItemClass("weaponclass1");
        ITEM_TYPE_WEAPON_1 = new RpgItemTypeImpl("weapon1", null, WEAPON_CLASS_1, 10, 0);
        WEAPON_CLASS_2 = new ItemClass("weaponclass2");
        ITEM_TYPE_WEAPON_2 = new RpgItemTypeImpl("weapon2", null, WEAPON_CLASS_2, 11, 0);
        ARMOR_TYPE_1 = new RpgItemTypeImpl("armor1", null, ItemClass.ARMOR, 0, 100);


        WEAPON_CLASS_1.getItems().add(ITEM_TYPE_WEAPON_1);
        WEAPON_CLASS_2.getItems().add(ITEM_TYPE_WEAPON_2);
        ItemClass.ARMOR.getItems().add(ARMOR_TYPE_1);

        CLASS_PRIMARY = new ClassDefinition("primary", "Primary");
        CLASS_TERTIARY = new ClassDefinition("tertiary", "Tertiary");
        CLASS_SECONDARY = new ClassDefinition("secondary", "Secondary");
        STR = new AttributeConfig("str", "str", 100, false, new HashMap<>(), null, null);
        AGI = new AttributeConfig("agi", "agi", 100, false, new HashMap<>(), null, null);
        AGI.getPropBonus().put(6, 1f);
        STR.getPropBonus().put(5, 2f);

    }
}
