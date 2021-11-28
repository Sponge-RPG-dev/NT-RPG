package cz.neumimto.rpg.spigot.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@AutoService(NTScriptProxy.class)
public class Targetting implements NTScriptProxy {

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private SpigotEntityService spigotEntityService;

    private SplittableRandom random = new SplittableRandom();

    @Function("find_nearby_entities")
    @Handler
    public Collection<IEntity> find(@NamedParam("e|entity") ISpigotEntity around,
                                    @NamedParam("r|radius") double radius,
                                    @NamedParam("d|damageCheck") boolean damageCheck) {
        Set<IEntity> entities = new HashSet<>();
        LivingEntity entity = around.getEntity();
        for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof LivingEntity livingEntity) {
                if (livingEntity.isDead()) {
                    continue;
                }
                if (damageCheck && !damageService.canDamage(around, livingEntity)) {
                    continue;
                }
                entities.add(spigotEntityService.get(livingEntity));
            }
        }
        return entities;
    }

    @Handler
    @Function("targetted_entity")
    public IEntity getTarget(@NamedParam("e|entity_from") ISpigotEntity entity,
                             @NamedParam("d|distance") double maxDistance) {
        if (maxDistance <= 0.0) {
            return null;
        }

        LivingEntity entity1 = entity.getEntity();


        RayTraceResult rayTraceResult = entity1.getWorld()
                .rayTraceEntities(
                        entity1.getEyeLocation(),
                        entity1.getEyeLocation().getDirection(),
                        maxDistance,
                        w -> w != entity1);
        if (rayTraceResult != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            if (hitEntity != null) {
                if (hitEntity instanceof LivingEntity) {
                    return spigotEntityService.get((LivingEntity) hitEntity);
                }
            }
        }
        return null;
    }

    @Handler
    @Function("find_random_entity")
    public IEntity findRandomEntity(@NamedParam("e|entity") ISpigotEntity around,
                                          @NamedParam("r|radius") double radius,
                                          @NamedParam("d|damageCheck") boolean damageCheck) {
        List<IEntity> iEntities = new ArrayList<>(find(around, radius, damageCheck));
        if (iEntities.size() > 0) {
            return iEntities.get(random.nextInt(iEntities.size()));
        }
        return null;
    }

}
