package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.skills.utils.Beam;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:dragonbreath")
public class Dragonbreath extends BeamSkill {

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        step = 1;
        onEntityHit = (target, tick, distance, caster, context, location) -> {
            LivingEntity d = (LivingEntity) target.getEntity();
            LivingEntity a = (LivingEntity) caster.getEntity();
            damageService.damage(a,d, EntityDamageEvent.DamageCause.FIRE, context.getDoubleNodeValue(SkillNodes.DAMAGE),false);
            return Beam.BeamActionResult.CONTINUE;
        };
        onTick = (tick, caster, distance, context, location) -> {
            location.getWorld().spawnParticle(Particle.FLAME, location, 3, 3,3,3);
            location.getWorld().spawnParticle(Particle.FLAME, location.add(0,0.5,0), 3, 4,4,4);
            location.getWorld().spawnParticle(Particle.ASH, location.add(0,0.5,0), 3, 2,2,2);
        };
    }
}
