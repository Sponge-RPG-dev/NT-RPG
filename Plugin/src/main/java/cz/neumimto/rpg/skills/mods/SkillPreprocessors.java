package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.events.skills.SkillPostUsageEvent;
import cz.neumimto.rpg.events.skills.SkillPrepareEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

public class SkillPreprocessors {

    public static ActiveSkillPreProcessorWrapper NOT_CASTABLE = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EARLY) {
        @Override
        public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
            skillResult.continueExecution(false).result(SkillResult.FAIL);
        }
    };


    public static ActiveSkillPreProcessorWrapper SKILL_COST = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
        @Override
        public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
            float requiredMana = skillResult.getFloatNodeValue(SkillNodes.MANACOST);
            float requiredHp = skillResult.getFloatNodeValue(SkillNodes.HPCOST);
            
            SkillPrepareEvent event = new SkillPrepareEvent(character, requiredHp, requiredMana);
            Sponge.getGame().getEventManager().post(event);
            if (event.isCancelled()) {
                skillResult.continueExecution(false);
                skillResult.next(character, info, skillResult.result(SkillResult.FAIL));
                return;
            }
            double hpcost = event.getRequiredHp() * NtRpgPlugin.GlobalScope.characterService.getCharacterProperty(character, DefaultProperties.health_cost_reduce);
            double manacost = event.getRequiredMana() * NtRpgPlugin.GlobalScope.characterService.getCharacterProperty(character, DefaultProperties.mana_cost_reduce);
            
            //todo float staminacost =
            if (character.getHealth().getValue() > hpcost) {
                if (character.getMana().getValue() >= manacost) {
                    skillResult.next(character, info, skillResult);     
                    return;
                }
                skillResult.continueExecution(false);
                skillResult.next(character, info, skillResult.result(SkillResult.NO_MANA));
                return;
            }
            skillResult.continueExecution(false);
            skillResult.next(character, info, skillResult.result(SkillResult.NO_HP));
            return;
        }
    };
    
    public static ActiveSkillPreProcessorWrapper RESOLVE_SKILLRESULT = new ActiveSkillPreProcessorWrapper(PreProcessorTarget.LATEST) {
        @Override
        public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
            SkillResult result = skillResult.getResult();
            if (result != SkillResult.OK) {
                skillResult.next(character, info, skillResult.result(result));
                return;
            } else {
                float newCd = skillResult.getLongNodeValue(SkillNodes.COOLDOWN);
                SkillPostUsageEvent eventt = new SkillPostUsageEvent(character,
                        skillResult.getFloatNodeValue(SkillNodes.HPCOST),
                        skillResult.getFloatNodeValue(SkillNodes.MANACOST),
                        newCd);
                Sponge.getGame().getEventManager().post(eventt);
                if (!eventt.isCancelled()) {
                    double newval = character.getHealth().getValue() - eventt.getHpcost();
                    if (newval <= 0) {
                        character.getPlayer().damage(Double.MAX_VALUE, DamageSource.builder()
                                .absolute()
                                .bypassesArmor()
                                .build());
                    } else {
                        character.getHealth().setValue(newval);
                        newCd = eventt.getCooldown() * NtRpgPlugin.GlobalScope.characterService.getCharacterProperty(character, DefaultProperties.cooldown_reduce);
                        character.getMana().setValue(character.getMana().getValue() - eventt.getManacost());
                        long cd = (long) newCd;
                        character.getCooldowns().put(info.getSkill().getName(), cd + System.currentTimeMillis());

                        Gui.displayMana(character);
                        skillResult.next(character, info, skillResult.result(result));
                        return;
                    }
                }
            }
        }
    };
    

}
