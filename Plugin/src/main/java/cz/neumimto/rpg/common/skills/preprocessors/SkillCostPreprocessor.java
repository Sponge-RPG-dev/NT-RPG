package cz.neumimto.rpg.common.skills.preprocessors;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.api.events.skill.SkillPreUsageEvent;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.entity.CommonProperties;

public abstract class SkillCostPreprocessor extends ActiveSkillPreProcessorWrapper {
    public SkillCostPreprocessor() {
        super(PreProcessorTarget.BEFORE);
    }

    @Override
    public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        SkillPreUsageEvent eventPre = Rpg.get().getEventFactory().createEventInstance(SkillPreUsageEvent.class);
        eventPre.setSkillContext(skillContext);
        eventPre.setSkill(skillContext.getSkill());
        eventPre.setCaster(character);

        if (Rpg.get().postEvent(eventPre)) {
            skillContext.continueExecution(false);
            skillContext.next(character, info, skillContext.result(SkillResult.FAIL));
            return;
        }
        EntityService<IEntity> entityService = Rpg.get().getEntityService();
        //Calculate those only for cast availability check
        float hpCostPre = skillContext.getFloatNodeValue(SkillNodes.HPCOST)
                * entityService.getEntityProperty(character, CommonProperties.health_cost_reduce);
        float manaCostPre = skillContext.getFloatNodeValue(SkillNodes.MANACOST)
                * entityService.getEntityProperty(character, CommonProperties.mana_cost_reduce);

        //todo float staminacost =
        if (character.getHealth().getValue() < hpCostPre) {
            skillContext.endWith(character, info, skillContext.result(SkillResult.NO_HP));
            return;
        }
        if (character.getMana().getValue() < manaCostPre) {
            skillContext.endWith(character, info, skillContext.result(SkillResult.NO_MANA));
            return;
        }

        //execute skill startEffectScheduler
        skillContext.next(character, info, skillContext);
        //execute skill end

        SkillResult result = skillContext.getResult();
        if (result != SkillResult.OK) {
            skillContext.endWith(character, info, skillContext.result(result));
        } else {
            SkillPostUsageEvent eventPost = Rpg.get().getEventFactory().createEventInstance(SkillPostUsageEvent.class);
            eventPost.setSkillContext(skillContext);
            eventPost.setSkill(skillContext.getSkill());
            eventPost.setCaster(character);

            if (Rpg.get().postEvent(eventPost)) {
                return;
            }

            float hpCostPost = skillContext.getFloatNodeValue(SkillNodes.HPCOST)
                    * entityService.getEntityProperty(character, CommonProperties.health_cost_reduce);
            float manaCostPost = skillContext.getFloatNodeValue(SkillNodes.MANACOST)
                    * entityService.getEntityProperty(character, CommonProperties.mana_cost_reduce);

            double newHp = character.getHealth().getValue() - hpCostPost;
            if (newHp <= 0) {
                killCaster(character);
            } else {
                character.getHealth().setValue(newHp);
                character.getMana().setValue(character.getMana().getValue() - manaCostPost);

                float newCd = skillContext.getLongNodeValue(SkillNodes.COOLDOWN)
                        * entityService.getEntityProperty(character, CommonProperties.cooldown_reduce_mult);
                long cd = (long) newCd;
                cd = cd + System.currentTimeMillis();
                if (newCd > 59999L) {
                    character.getCharacterBase().getCharacterSkill(info.getSkill()).setCooldown(cd);
                }
                character.getCooldowns().put(info.getSkill().getName(), cd);
                Gui.displayMana(character);
                //skillResult.next(character, info, skillResult.result(result));
            }
        }
    }

    protected abstract void killCaster(IActiveCharacter character);
}
