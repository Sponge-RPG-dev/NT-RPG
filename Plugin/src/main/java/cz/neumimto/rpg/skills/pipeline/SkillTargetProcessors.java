package cz.neumimto.rpg.skills.pipeline;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.IEntityType;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.utils.TriConsumer;
import cz.neumimto.rpg.utils.Utils;
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
import java.util.function.Consumer;

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
    public static final BiFunction<IEntity, Float, List<IEntity>> NEARBY_ALLIES = ((entity, radius) -> {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius);
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
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                nearby.add(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    nearby.add(iEntity);
                }
            }
        }
        return nearby;
    });

    @SkillComponent(
            value = "Returns a list of nearby allies",
            usage = "for_each_nearby_ally(entity, radius, new Consumer() { apply: function(ally} { ... } )",
            params = {
                    @SkillComponent.Param("entity - allies for the entity"),
                    @SkillComponent.Param("radius"),
                    @SkillComponent.Param("allyEntity - callback")
            }
    )
    public static final TriConsumer<IEntity, Float, Consumer<IEntity>> FOR_EACH_NEARBY_ALLY = ((entity, radius, consumer) -> {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius);
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
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                consumer.accept(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    consumer.accept(iEntity);
                }
            }
        }
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
    public static final BiFunction<IEntity, Integer, IEntity> TARGETED_ENEMY = (caster, range) -> {
        if (caster.getType() == IEntityType.MOB) {

            Living entity = caster.getEntity();
            if (entity instanceof Monster) {
                Optional<Entity> target = ((Monster) entity).getTarget();
                if (target.isPresent()) {
                    Entity mtarget = target.get();
                    if (!(mtarget instanceof Living)) {
                        return null;
                    }
                    return NtRpgPlugin.GlobalScope.entityService.get(mtarget);
                }
            } else {
                Collection<Entity> nearbyEntities = entity.getNearbyEntities(range);

                throw new NotImplementedException(":(");
            }
        } else if (caster.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) caster;
            Living targettedEntity = Utils.getTargettedEntity(character, range);
            if (targettedEntity != null) {
                if (Utils.canDamage(character, targettedEntity)) {
                    return NtRpgPlugin.GlobalScope.entityService.get(targettedEntity);
                }
            }
        }
        return null;
    };

    @SkillComponent(
            value = "Do action for every nearby enemy",
            usage = "for_each_nearby_enemy(entity, radius, new Consumer() { apply: function(entity} { .. })",
            params = {
                    @SkillComponent.Param("entity - An entity which we search for its enemies"),
                    @SkillComponent.Param("range - Maximal search range"),
                    @SkillComponent.Param("@returns - An entity instance or null"),
            }
    )
    public static TriConsumer<IEntity, Float, Consumer<IEntity>> FOR_EACH_NEARBY_ENEMY = ((entity, radius) -> {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius);
        IActiveCharacter character = (IActiveCharacter) entity;
        for (Entity nearbyEntity : nearbyEntities) {
            IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
            if (!iEntity.isFriendlyTo(character)) {
                nearby.add(iEntity);
            }
        }
        return nearby;
    });

}
