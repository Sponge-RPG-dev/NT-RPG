package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.particles.StaticCircularBeamEffect;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:flamedash")
public class FlameDash extends TargetedBlockSkill {

    @Override
    public void init() {
        super.init();
    }


    @Override
    protected SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        long delay = skillContext.getLongNodeValue("delay");

        Location location = player.getLocation();

        StaticCircularBeamEffect staticCircularBeamEffect = new StaticCircularBeamEffect(SpigotRpgPlugin.getEffectManager());
        staticCircularBeamEffect.vertStep = 0.1;
        staticCircularBeamEffect.setLocation(location);
        staticCircularBeamEffect.vertVecMax = 3;
        staticCircularBeamEffect.vertVecMin = 1;
        staticCircularBeamEffect.particleCount = 15;
        staticCircularBeamEffect.radius = 1.3f;
        staticCircularBeamEffect.enableRotation = true;
        staticCircularBeamEffect.particle = Particle.FLAME;
        staticCircularBeamEffect.type = EffectType.REPEATING;

        SpigotRpgPlugin.getEffectManager().start(staticCircularBeamEffect);

        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(),
                () -> {
                    player.teleport(block.getLocation().add(0,1,0));

                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.75f, 0.75f);
                    player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 2);
                    player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);

                    staticCircularBeamEffect.cancel();

                    LineEffect lineEffect = new LineEffect(SpigotRpgPlugin.getEffectManager());
                    lineEffect.particle = Particle.LAVA;
                    lineEffect.particleCount = 10 * (int) location.distance(player.getLocation());
                    lineEffect.setLocation(location);
                    lineEffect.setTargetLocation(player.getLocation());
                    lineEffect.type = EffectType.INSTANT;

                    SpigotRpgPlugin.getEffectManager().start(lineEffect);
                }, delay);

        return SkillResult.OK;
    }


}
