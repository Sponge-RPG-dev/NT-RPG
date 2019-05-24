package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.events.skill.SkillPreUsageEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.properties.SpongeDefaultProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

public class SkillPreprocessors {

    public static ActiveSkillPreProcessorWrapper NOT_CASTABLE = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EARLY) {
        @Override
        public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
            skillResult.endWith(character, info, skillResult.result(SkillResult.FAIL));
        }
    };


    public static ActiveSkillPreProcessorWrapper SKILL_COST = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
        @Override
        public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
            SkillPreUsageEvent eventPre = new SkillPreUsageEvent(character, skillContext);
            if (Sponge.getGame().getEventManager().post(eventPre)) {
                skillContext.continueExecution(false);
                skillContext.next(character, info, skillContext.result(SkillResult.FAIL));
                return;
            }

            //Calculate those only for cast availability check
            float hpCostPre = skillContext.getFloatNodeValue(SkillNodes.HPCOST)
                    * NtRpgPlugin.GlobalScope.entityService.getEntityProperty(character, SpongeDefaultProperties.health_cost_reduce);
            float manaCostPre = skillContext.getFloatNodeValue(SkillNodes.MANACOST)
                    * NtRpgPlugin.GlobalScope.entityService.getEntityProperty(character, SpongeDefaultProperties.mana_cost_reduce);

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
                SkillPostUsageEvent eventPost = new SkillPostUsageEvent(character, skillContext);
                if (Sponge.getGame().getEventManager().post(eventPost)) return;

                float hpCostPost = skillContext.getFloatNodeValue(SkillNodes.HPCOST)
                        * NtRpgPlugin.GlobalScope.entityService.getEntityProperty(character, SpongeDefaultProperties.health_cost_reduce);
                float manaCostPost = skillContext.getFloatNodeValue(SkillNodes.MANACOST)
                        * NtRpgPlugin.GlobalScope.entityService.getEntityProperty(character, SpongeDefaultProperties.mana_cost_reduce);

                double newHp = character.getHealth().getValue() - hpCostPost;
                if (newHp <= 0) {
                    character.getPlayer().damage(Double.MAX_VALUE, DamageSource.builder()
                            .absolute()
                            .bypassesArmor()
                            .build());
                } else {
                    character.getHealth().setValue(newHp);
                    character.getMana().setValue(character.getMana().getValue() - manaCostPost);

                    float newCd = skillContext.getLongNodeValue(SkillNodes.COOLDOWN)
                            * NtRpgPlugin.GlobalScope.entityService.getEntityProperty(character, SpongeDefaultProperties.cooldown_reduce_mult);
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
    };
}
