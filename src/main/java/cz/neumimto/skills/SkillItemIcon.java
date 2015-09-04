package cz.neumimto.skills;

import org.spongepowered.api.item.ItemType;

/**
 * Created by ja on 31.8.2015.
 */
public class SkillItemIcon {
    public ItemType itemType;
    public String skillName;

    public ISkill skill;

    public SkillItemIcon(ISkill skill) {
        this.skillName = skill.getName();
        this.skill = skill;
    }


}
