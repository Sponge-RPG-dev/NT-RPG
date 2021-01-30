package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:swapposition")
public class SwapPosition extends TargetedEntitySkill {

    @Inject
    private SpigotDamageService spigotDamageService;

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {

        if (!(target instanceof LivingEntity)) {
            return SkillResult.CANCELLED;
        }
        LivingEntity lT = (LivingEntity) target.getEntity();

        if (!target.isFriendlyTo(source) || !damageService.canDamage(source, target)) {
            return SkillResult.CANCELLED;
        }

        Player caster = source.getPlayer();
        Location targetLoc = lT.getLocation();
        playVisualEffect(targetLoc);

        Location casterLoc = caster.getLocation();
        playVisualEffect(casterLoc);

        caster.teleport(targetLoc, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
        lT.teleport(casterLoc);



        return SkillResult.OK;
    }

    protected void playVisualEffect(Location location) {
        World world = location.getWorld();
        world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        world.spawnParticle(Particle.PORTAL, location, 20, 5, 3, 5);
    }
}
