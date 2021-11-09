package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.common.skills.ISkillNode;
import cz.neumimto.rpg.common.skills.SkillData;

public interface ValidatedMechanic {
    default boolean isValid(SkillData context, ISkillNode node) {
        return isValid(context, node.value());
    }

    default boolean isValid(SkillData context, String string) {
        return context.getSkillSettings() != null
                && context.getSkillSettings().hasNode(string);
    }
}
