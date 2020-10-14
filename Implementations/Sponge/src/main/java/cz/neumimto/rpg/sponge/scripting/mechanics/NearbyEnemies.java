package cz.neumimto.rpg.sponge.scripting.mechanics;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.TargetSelector;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
@TargetSelector("nearby_enemies")
public class NearbyEnemies {

    @Inject
    private SpongeDamageService damageService;

    @Inject
    private SpongeEntityService spongeEntityService;


    @Handler
    public Set<IEntity> getTargets(@Caster ISpongeCharacter character, @SkillArgument("settings.radius") float radius) {
        Set<IEntity> entities = new HashSet<>();
        Player player = character.getPlayer();
        for (Entity nearbyEntity : player.getNearbyEntities(radius)) {
            if (nearbyEntity instanceof Living) {
                Living livingEntity = (Living) nearbyEntity;
                if (damageService.canDamage(character, livingEntity)) {
                    IEntity iEntity = spongeEntityService.get(livingEntity);
                    entities.add(iEntity);
                }
            }
        }
        return entities;
    }
}

