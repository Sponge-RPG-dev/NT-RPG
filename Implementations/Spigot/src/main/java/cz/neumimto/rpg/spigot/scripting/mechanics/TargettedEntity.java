package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TargettedEntity {

    @Inject
    private SpigotEntityService entityService;

    public IEntity getTarget(ISpigotCharacter character, float maxDistance) {
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
