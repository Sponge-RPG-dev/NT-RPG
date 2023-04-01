package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.effects.common.ManaShieldEffect;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:manashield")
public class ManaShield extends ActiveSkill<SpigotCharacter> {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.AMPLIFIER, "0.75");
        settings.addExpression(SkillNodes.DURATION, "-1");
        addSkillType(SkillType.BUFF);
        addSkillType(SkillType.PROTECTION);
    }

    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext info) {
        if (character.hasEffect(ManaShieldEffect.name)) {
            effectService.removeEffect(ManaShieldEffect.name, character, this);
            return SkillResult.OK_NO_COOLDOWN;
        }
        long duration = info.getLongNodeValue(SkillNodes.DURATION);
        double mult = info.getLongNodeValue(SkillNodes.MULTIPLIER);
        ManaShieldEffect effect = new ManaShieldEffect(character, duration, mult);
        effectService.addEffect(effect, this, character);
        return SkillResult.OK;
    }
}
