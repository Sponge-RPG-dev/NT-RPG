package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.spigot.effects.common.PiggifyEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:piggify")
public class Piggify extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DURATION, "level * 500 + 3500");
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        long duration = info.getLongNodeValue(SkillNodes.DURATION);
        PiggifyEffect piggifyEffect = new PiggifyEffect(target, duration);
        effectService.addEffect(piggifyEffect);
        return SkillResult.OK;
    }
}
