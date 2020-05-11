package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.api.skills.ISkillNode;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;

public abstract class SkillCostPipelineMechanic implements ISkillCostMechanic {

    boolean isValid(SkillData context, ISkillNode node) {
        return isValid(context, node.value()) && isValid(context, node.value() + SkillSettings.BONUS_SUFFIX);
    }

    boolean isValid(SkillData context, String string) {
        return context.getSkillSettings() != null && context.getSkillSettings().getNodes().containsKey(string) && context.getSkillSettings().getNodeValue(string) > 0f;
    }

}
