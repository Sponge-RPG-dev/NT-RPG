package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.scripting.mechanics.SpigotEntityUtils;
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
    private SpigotEntityUtils damageMechanic;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DAMAGE, "10 + level * 2");
        setDamageType(EntityDamageEvent.DamageCause.FIRE.name());
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.FIRE);
        step = 1;
        onEntityHit = (target, tick, distance, caster, context, location) -> {
            damageMechanic.damage(target, caster, context.getDoubleNodeValue(SkillNodes.DAMAGE), 0, EntityDamageEvent.DamageCause.FIRE, this);
            return Beam.BeamActionResult.CONTINUE;
        };
        onTick = (tick, caster, distance, context, location) -> {
            location.getWorld().spawnParticle(Particle.FLAME, location, 3, 0,0,0);
            location.getWorld().spawnParticle(Particle.FLAME, location.add(0,0.5,0), 3, 0,0,0);
            location.getWorld().spawnParticle(Particle.ASH, location.add(0,0.5,0), 3, 0,0,0);
        };
    }
}

