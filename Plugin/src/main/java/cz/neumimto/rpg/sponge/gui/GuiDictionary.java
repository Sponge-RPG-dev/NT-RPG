package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.skills.ISkill;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.HashMap;
import java.util.Map;

public class GuiDictionary {

    private static Map<String, ItemType> skillIcons = new HashMap<>();

    public static void addSkillIcon(ISkill skill, ItemType itemType) {
        addSkillIcon(skill.getId(), itemType);
    }

    public static void addSkillIcon(String skill, ItemType itemType) {
        skillIcons.put(skill, itemType);
    }

    public static void getSkillIcon(ISkill skill) {
        getSkillIcon(skill.getId());
    }

    public static ItemType getSkillIcon(String skill) {
        return skillIcons.getOrDefault(skill, ItemTypes.STONE);
    }
}
