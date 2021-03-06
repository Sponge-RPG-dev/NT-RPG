package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
@TargetSelector("nearby_enemies")
public class NearbyEnemies {

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private SpigotEntityService spigotEntityService;


    @Handler
    public Set<IEntity> getTargets(@Caster ISpigotCharacter character, @SkillArgument("settings.radius") float radius) {
        Set<IEntity> entities = new HashSet<>();
        Player player = character.getPlayer();
        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(character, livingEntity)) {
                    IEntity iEntity = spigotEntityService.get(livingEntity);
                    entities.add(iEntity);
                }
            }
        }
        return entities;
    }
}
