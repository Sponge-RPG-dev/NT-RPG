package cz.neumimto.rpg.spigot.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import javax.inject.Singleton;

import static cz.neumimto.nts.annotations.ScriptMeta.*;

@Singleton
@AutoService(NTScriptProxy.class)
public class Particles implements NTScriptProxy {

    @Handler
    @Function("lightning")
    public void spawnLigting(@NamedParam("e|at_entity") ISpigotEntity entity) {
        Location location = entity.getEntity().getLocation();
        location.getWorld().strikeLightningEffect(location);
    }

    @Handler
    @Function("sound")
    public void sound(@NamedParam("s|sound") Sound sound,
                      @NamedParam("l|location") Location l,
                      @NamedParam("v|volume") float volume,
                      @NamedParam("p|pitch") float pitch) {
        l.getWorld().playSound(l, sound, volume, pitch);

    }

    @Handler
    @Function("particle")
    public void spawnParticle(@NamedParam("e|at_entity") ISpigotEntity entity,
                              @NamedParam("l|at_location") Location location,
                              @NamedParam("p|particle") Particle particle,
                              @NamedParam("a|amount") int amount,
                              @NamedParam("oy|offset_y") double offsetY,
                              @NamedParam("ox|offset_x") double offsetX,
                              @NamedParam("oz|offset_z") double offsetZ,
                              @NamedParam("ex|extra") double extra,
                              @NamedParam("m|m_data") Material material
    ) {
        Location pLoc = location;
        if (pLoc == null) {
            pLoc = entity.getEntity().getLocation();
        }
        BlockData data = null;
        if (material != null) {
            data = material.createBlockData();
        }
        pLoc.getWorld().spawnParticle(particle, location, amount, offsetX, offsetY, offsetZ, extra, data);
    }

    @Handler
    @Function("particle_line")
    public void particleLine(@NamedParam("ef|entity_from") ISpigotEntity entityFrom,
                             @NamedParam("lf|location_from") Location locationFrom,
                             @NamedParam("et|entity_to") ISpigotEntity entityTo,
                             @NamedParam("lt|location_to") Location locationTo,

                             @NamedParam("c|particle_count") int particleCount,
                             @NamedParam("s|step") double step,
                             @NamedParam("zz|zigzag") boolean zz,
                             @NamedParam("l|length") double length,
                             @NamedParam("zx|zz_x") double zzX,
                             @NamedParam("zy|zz_y") double zzY,
                             @NamedParam("zz|zz_z") double zzZ,
                             @NamedParam("zzc|zz_count") int zzs,

                             @NamedParam("pt|particle") Particle particle,
                             @NamedParam("oy|offset_y") double offsetY,
                             @NamedParam("ox|offset_x") double offsetX,
                             @NamedParam("oz|offset_z") double offsetZ,
                             @NamedParam("m|m_data") Material material) {
        Location pLoc = locationFrom;
        if (pLoc == null) {
            pLoc = entityFrom.getEntity().getLocation();
        }


        Location tLoc = locationTo;
        if (tLoc == null) {
            tLoc = entityTo.getEntity().getLocation();
        }

        LineEffect lineEffect = new LineEffect(SpigotRpgPlugin.getEffectManager());
        lineEffect.particle = particle;
        lineEffect.particleCount = particleCount == 0 ? (int) (pLoc.distance(tLoc) * step) : particleCount;
        lineEffect.particleOffsetX = (float) offsetX;
        lineEffect.particleOffsetY = (float) offsetY;
        lineEffect.particleOffsetZ = (float) offsetZ;
        lineEffect.isZigZag = zz;
        lineEffect.zigZags = zzs;
        lineEffect.zigZagOffset = new Vector(zzX, zzY, zzZ);
        lineEffect.length = length;
        lineEffect.material = material;
        lineEffect.setLocation(pLoc);
        lineEffect.setTargetLocation(tLoc);
        lineEffect.type = EffectType.INSTANT;

        SpigotRpgPlugin.getEffectManager().start(lineEffect);
    }
}
