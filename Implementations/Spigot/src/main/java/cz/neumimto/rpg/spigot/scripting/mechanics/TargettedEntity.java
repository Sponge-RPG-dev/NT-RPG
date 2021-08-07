package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@TargetSelector("targeted_entity")
public class TargettedEntity {

    @Inject
    private SpigotEntityService entityService;

    @Handler
    public IEntity getTarget(@Caster ISpigotCharacter character, @SkillArgument("$settings.range") float maxDistance) {
        Player player = character.getPlayer();
        if (maxDistance <= 0.0) {
            return null;
        }
        RayTraceResult rayTraceResult = player.getWorld()
                .rayTraceEntities(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        maxDistance,
                        entity -> entity != player);
        if (rayTraceResult != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            if (hitEntity != null) {
                if (hitEntity instanceof LivingEntity) {
                    return entityService.get((LivingEntity) hitEntity);
                }
            }
        }
        return null;
    }
}
