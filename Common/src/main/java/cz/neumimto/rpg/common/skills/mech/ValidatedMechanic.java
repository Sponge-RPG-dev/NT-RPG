package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.skills.ISkillNode;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;

public interface ValidatedMechanic {
    default boolean isValid(SkillData context, ISkillNode node) {
        return isValid(context, node.value());
    }

    default boolean isValid(SkillData context, String string) {
        return context.getSkillSettings() != null
                && context.getSkillSettings().hasNode(string);
    }
}
