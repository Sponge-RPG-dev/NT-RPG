package cz.neumimto.rpg.spigot.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.nms.NMSHandler;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import static cz.neumimto.nts.annotations.ScriptMeta.Handler;
import static cz.neumimto.nts.annotations.ScriptMeta.NamedParam;

@Singleton
@AutoService(NTScriptProxy.class)
public class SpigotEntityUtils implements NTScriptProxy {

    @Inject
    private NMSHandler nmsHandler;

    @Handler
    @ScriptMeta.Function("get_location")
    public Location getLocation(
            @NamedParam("e|entity") IEntity e
    ) {
        return ((LivingEntity)e.getEntity()).getLocation();
    }

    @Handler
    @ScriptMeta.Function("teleport")
    public void teleport(
            @NamedParam("e|entity") IEntity e,
            @NamedParam("l|location") Location l,
            @NamedParam("p|pitch") float pitch,
            @NamedParam("y|yaw") float yaw,
            @NamedParam("rbf|relativeBlockFace") BlockFace blockFace

    ) {
        if (blockFace != null) {
            l = l.getBlock().getRelative(blockFace).getLocation();
        }
        if (pitch != 0) {
            l.setPitch(pitch);
        }
        if (yaw != 0) {
            l.setYaw(yaw);
        }
        ((LivingEntity) e.getEntity()).teleport(l, PlayerTeleportEvent.TeleportCause.COMMAND);

    }

    @Handler
    @ScriptMeta.Function("set_velocity")
    public void setVelocity(
            @NamedParam("t|target") IEntity target,
            @NamedParam("v|vector") Vector v
    ) {
        LivingEntity livingEntity = (LivingEntity) target.getEntity();
        livingEntity.setVelocity(v);
    }

    @Handler
    @ScriptMeta.Function("damage")
    public boolean damage(
            @NamedParam("t|target") ISpigotEntity target,
            @NamedParam("e|damager") ISpigotEntity damager,
            @NamedParam("d|damage") double damage,
            @NamedParam("k|knockback") double knockback,
            @NamedParam("c|cause") EntityDamageEvent.DamageCause cause,
            @NamedParam("s|skill") ISkill e) {

        LivingEntity tEntity = target.getEntity();

        if (tEntity.getHealth() <= 0) {
            return false;
        }
        target.setSkillOrEffectDamageCause(e);
        LivingEntity dEntity = damager.getEntity();

        if (cause == null && e.getDamageType() != null) {
            cause = EntityDamageEvent.DamageCause.valueOf(e.getDamageType());
        } else {
            cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        }

        EntityDamageEvent event = nmsHandler.handleEntityDamageEvent(tEntity, dEntity, cause, damage, knockback);
        return !event.isCancelled() && event.getFinalDamage() > 0;
    }


}
