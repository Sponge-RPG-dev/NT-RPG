package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;
import cz.neumimto.skills.effects.negative.StunEffect;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:bash")
public class Bash extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.CONTACT.name());

        settings.addNode(SkillNodes.DAMAGE, 15, 5);
        settings.addNode(SkillNodes.DURATION, 5000, 500);
    }

    @Override
    public void castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info, SkillContext skillContext) {
        LivingEntity entity = (LivingEntity) target.getEntity();
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        if (damage > 0) {
            damageService.damage(entity, source.getEntity(), EntityDamageEvent.DamageCause.CONTACT, damage, false);
        }
        long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
        StunEffect stunEffect = new StunEffect(target, duration);
        effectService.addEffect(stunEffect, this);
        skillContext.next(source, info, skillContext.result(SkillResult.OK));
    }

}
