package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.effects.common.ManaShieldEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:manashield")
public class ManaShield extends ActiveSkill<ISpigotCharacter> {

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
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
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
