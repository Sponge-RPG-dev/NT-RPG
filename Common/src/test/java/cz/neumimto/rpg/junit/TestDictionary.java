package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.items.ItemClass;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.items.RpgItemTypeImpl;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;

import java.util.HashMap;

public class TestDictionary {

    public static final ISkill DUMMY_SKILL = new ActiveSkill() {
        @Override
        public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
            return SkillResult.OK;
        }
    };
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
        ITEM_TYPE_WEAPON_1 = new RpgItemTypeImpl("weapon1", null, WEAPON_CLASS_1, 10, 0, null);
        WEAPON_CLASS_2 = new ItemClass("weaponclass2");
        ITEM_TYPE_WEAPON_2 = new RpgItemTypeImpl("weapon2", null, WEAPON_CLASS_2, 11, 0, null);
        ARMOR_TYPE_1 = new RpgItemTypeImpl("armor1", null, ItemClass.ARMOR, 0, 100, null);


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
