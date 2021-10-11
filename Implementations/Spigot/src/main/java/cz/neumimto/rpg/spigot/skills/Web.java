package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.effects.common.WebEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:web")
public class Web extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DURATION, "level * 1.5 + 7000");
        addSkillType(SkillType.DAMAGE_CHECK_TARGET);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        long duration = info.getLongNodeValue(SkillNodes.DURATION);
        WebEffect webEffect = new WebEffect(target, duration);
        effectService.addEffect(webEffect, this, source);
        return SkillResult.OK;
    }
}
