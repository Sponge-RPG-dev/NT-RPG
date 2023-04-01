package cz.neumimto.rpg.common.skills;


import com.google.inject.Injector;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.common.events.skill.SkillPreUsageEvent;
import cz.neumimto.rpg.common.skills.processors.ISkillCondition;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.ServiceLoader;

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

        ServiceLoader.load(ISkillCondition.class, getClass().getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::type)
                .map(injector::getInstance)
                .filter(a -> a.isValidForContext(skillData))
                .forEach(a -> conditions = push(conditions, a));

        ServiceLoader.load(ISkillCastMechanic.class, getClass().getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::type)
                .map(injector::getInstance)
                .filter(a -> a.isValidForContext(skillData))
                .forEach(a -> skillCost = push(skillCost, a));

        return this;
    }

    private static <T> T[] push(T[] arr, T item) {
        T[] tmp = Arrays.copyOf(arr, arr.length + 1);
        tmp[tmp.length - 1] = item;
        return tmp;
    }

    @Override
    public SkillResult execute(ActiveCharacter character, PlayerSkillContext playerSkillContext) {
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
