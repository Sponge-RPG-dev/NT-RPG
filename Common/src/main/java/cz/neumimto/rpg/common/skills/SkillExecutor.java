package cz.neumimto.rpg.common.skills;


import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.api.events.skill.SkillPreUsageEvent;
import cz.neumimto.rpg.api.skills.ISkillExecutor;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.skills.processors.ISkillCondition;
import cz.neumimto.rpg.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class SkillExecutor implements ISkillExecutor {

    @Inject
    private Injector injector;

    @Inject
    private EventFactoryService eventFactory;

    private ISkillCastMechanic[] skillCost;

    private ISkillCondition[] conditions;

    @Override
    public ISkillExecutor init(SkillData skillData) {
        skillCost = new ISkillCastMechanic[0];
        conditions = new ISkillCondition[0];

        for (Key<?> key : injector.getAllBindings().keySet()) {
            if (ISkillCondition.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
                ISkillCondition skillCondition = (ISkillCondition) injector.getInstance(key);
                if (skillCondition.isValidForContext(skillData)) {
                    conditions = push(conditions, skillCondition);
                }
            }
            if (ISkillCastMechanic.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
                ISkillCastMechanic m = (ISkillCastMechanic) injector.getInstance(key);
                if (m.isValidForContext(skillData)) {
                    skillCost = push(skillCost, m);
                }
            }
        }

        return this;
    }

    private static <T> T[] push(T[] arr, T item) {
        T[] tmp = Arrays.copyOf(arr, arr.length + 1);
        tmp[tmp.length - 1] = item;
        return tmp;
    }

    @Override
    public SkillResult execute(IActiveCharacter character, PlayerSkillContext playerSkillContext) {
        SkillPreUsageEvent eventPre = eventFactory.createEventInstance(SkillPreUsageEvent.class);
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
        for (ISkillCastMechanic expm : skillCost) {
            result = expm.processBefore(character, playerSkillContext);
            if (result != SkillResult.OK) {
                expm.notifyFailure(character, playerSkillContext);
                return result;
            }
        }

        result = playerSkillContext.getSkill().onPreUse(character, playerSkillContext);

        if (result == SkillResult.OK) {
            SkillPostUsageEvent eventPost = eventFactory.createEventInstance(SkillPostUsageEvent.class);
            eventPost.setSkill(playerSkillContext.getSkill());
            eventPost.setCaster(character);
            if (!Rpg.get().postEvent(eventPost)) {
                for (ISkillCastMechanic expm : skillCost) {
                    expm.processAfterSuccess(character, playerSkillContext);
                }
            }
        }

        return result;
    }

}
