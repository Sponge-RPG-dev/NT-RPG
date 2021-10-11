package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.scripting.JsBinding;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;


@JsBinding(JsBinding.Type.CONTAINER)
public class SkillTargetProcessors {

    public static final BiFunction<IEntity<LivingEntity>, Number, List<IEntity>> NEARBY_ALLIES = ((entity, radius) -> {
        double v = radius.doubleValue();
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(v, v, v);
        List<IEntity> nearby = new ArrayList<>();
        if (entity.getType() == IEntityType.MOB) {
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity.getType() == EntityType.PLAYER) {
                    continue;
                }
                if (!(nearbyEntity instanceof LivingEntity)) {
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


    public static final BiFunction<IEntity<? extends LivingEntity>, Number, IEntity> TARGETED_ENEMY = (caster, range) -> {
        if (caster.getType() == IEntityType.MOB) {

            LivingEntity entity = caster.getEntity();
            if (entity instanceof Monster) {
                LivingEntity target = ((Monster) entity).getTarget();
                if (target != null) {
                    return Rpg.get().getEntityService().get(target);
                }
            } else {
                return null;
            }
        } else if (caster instanceof ISpigotCharacter) {
            ISpigotCharacter character = (ISpigotCharacter) caster;
            LivingEntity targetedEntity = TargetedEntitySkill.rayTraceEntity(character.getPlayer(), range.doubleValue());
            if (targetedEntity != null) {
                SpigotDamageService damageService = (SpigotDamageService) Rpg.get().getDamageService();;
                if (damageService.canDamage(character, targetedEntity)) {
                    return Rpg.get().getEntityService().get(targetedEntity);
                }
            }
        }
        return null;
    };

}
