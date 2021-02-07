package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.effects.common.PiggifyEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
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
        new PiggifyEffect(target, duration);

        return SkillResult.OK;
    }
}
