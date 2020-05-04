package cz.neumimto.rpg.common.skills;


import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.api.events.skill.SkillPreUsageEvent;
import cz.neumimto.rpg.api.skills.ISkillExecutor;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.skills.preprocessors.ISkillCondition;
import cz.neumimto.rpg.common.skills.reagents.Cooldown;
import cz.neumimto.rpg.common.skills.reagents.HPCost;
import cz.neumimto.rpg.common.skills.reagents.ISkillCostMechanic;
import cz.neumimto.rpg.common.skills.reagents.ManaCost;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SkillExecutor implements ISkillExecutor {

    @Inject
    private Injector injector;

    @Inject
    private ManaCost manaCost;

    @Inject
    private Cooldown cooldown;

    @Inject
    private HPCost hpCost;

    private List<ISkillCostMechanic> skillCost;

    private List<ISkillCondition> conditions;

    @Override
    public ISkillExecutor init(SkillData skillData) {
        skillCost = new LinkedList<>();
        conditions = new LinkedList<>();

        if (cooldown.isValidForContext(skillData)) {
            skillCost.add(cooldown);
        }
        if (manaCost.isValidForContext(skillData)) {
            skillCost.add(manaCost);
        }
        if (hpCost.isValidForContext(skillData)) {
            skillCost.add(hpCost);
        }

        Set<ISkillCondition> allConds = new HashSet<>();
        for (Key<?> key : injector.getAllBindings().keySet()) {
            if (ISkillCondition.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
                ISkillCondition skillCondition = (ISkillCondition) injector.getInstance(key);
                allConds.add(skillCondition);
            }
        }

        for (ISkillCondition skillCondition : allConds) {
            if (skillCondition.isValidForContext(skillData)) {
                conditions.add(skillCondition);
            }
        }

        return this;
    }

    @Override
    public SkillResult execute(IActiveCharacter character, PlayerSkillContext playerSkillContext) {
        SkillPreUsageEvent eventPre = Rpg.get().getEventFactory().createEventInstance(SkillPreUsageEvent.class);
        eventPre.setSkill(playerSkillContext.getSkill());
        eventPre.setCaster(character);

        if (Rpg.get().postEvent(eventPre)) {
            return SkillResult.FAIL;
        }

        for (ISkillCondition condition : conditions) {
            if (!condition.check(character, playerSkillContext)) {
                return SkillResult.FAIL;
            }
        }

        SkillResult result;
        for (ISkillCostMechanic expm : skillCost) {
            result = expm.processBefore(character, playerSkillContext);
            if (result != SkillResult.OK) {
                return result;
            }
        }

        result = playerSkillContext.getSkill().onPreUse(character, playerSkillContext);

        if (result == SkillResult.OK) {
            for (ISkillCostMechanic expm : skillCost) {
                expm.processAfterSuccess(character, playerSkillContext);
            }
        }
        SkillPostUsageEvent eventPost = Rpg.get().getEventFactory().createEventInstance(SkillPostUsageEvent.class);
        eventPost.setSkill(playerSkillContext.getSkill());
        eventPost.setCaster(character);

        Rpg.get().postEvent(eventPost);

        return result;
    }

}
