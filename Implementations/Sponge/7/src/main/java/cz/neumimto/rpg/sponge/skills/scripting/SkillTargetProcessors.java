package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@JsBinding(JsBinding.Type.CONTAINER)
public class SkillTargetProcessors {

    @SkillComponent(
            value = "Returns a list of nearby allies",
            usage = "var list = nearby_allies(entity, radius)",
            params = {
                    @SkillComponent.Param("entity - allies for the entity"),
                    @SkillComponent.Param("radius")
            }
    )
    public static final BiFunction<IEntity<Living>, Number, List<IEntity>> NEARBY_ALLIES = ((entity, radius) -> {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius.doubleValue());
        List<IEntity> nearby = new ArrayList<>();
        if (entity.getType() == IEntityType.MOB) {
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity.getType() == EntityTypes.PLAYER) {
                    continue;
                }
                if (!(nearbyEntity instanceof Living)) {
                    continue;
                }
                if (nearbyEntity.get(Keys.TAMED_OWNER).isPresent()) {
                    continue;
                }
                IEntity iEntity = Rpg.get().getEntityService().get(nearbyEntity);
                nearby.add(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = Rpg.get().getEntityService().get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    nearby.add(iEntity);
                }
            }
        }
        return nearby;
    });


    @SkillComponent(
            value = "Returns current enemy entity in crosshair",
            usage = "var target = targeted_enemy(entity, range)",
            params = {
                    @SkillComponent.Param("entity - An entity which we search for its enemies"),
                    @SkillComponent.Param("range - Maximal search range"),
                    @SkillComponent.Param("@returns - An entity instance or null"),
            }
    )
    public static final BiFunction<IEntity<? extends Living>, Number, IEntity> TARGETED_ENEMY = (caster, range) -> {
        if (caster.getType() == IEntityType.MOB) {

            Living entity = caster.getEntity();
            if (entity instanceof Monster) {
                Optional<Entity> target = ((Monster) entity).getTarget();
                if (target.isPresent()) {
                    Entity mtarget = target.get();
                    if (!(mtarget instanceof Living)) {
                        return null;
                    }
                    return Rpg.get().getEntityService().get(mtarget);
                }
            } else {
                Collection<Entity> nearbyEntities = entity.getNearbyEntities(range.doubleValue());

                throw new NotImplementedException(":(");
            }
        } else if (caster.getType() == IEntityType.CHARACTER) {
            ISpongeCharacter character = (SpongeCharacter) caster;
            Living targetedEntity = Utils.getTargetedEntity(character, range.intValue());
            if (targetedEntity != null) {
                if (Utils.canDamage(character, targetedEntity)) {
                    return Rpg.get().getEntityService().get(targetedEntity);
                }
            }
        }
        return null;
    };

}
