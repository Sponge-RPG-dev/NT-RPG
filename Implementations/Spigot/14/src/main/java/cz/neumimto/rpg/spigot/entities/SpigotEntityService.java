package cz.neumimto.rpg.spigot.entities;

import static cz.neumimto.rpg.api.logging.Log.info;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IMob;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

import javax.inject.Singleton;

@Singleton
public class SpigotEntityService extends AbstractEntityService<LivingEntity, SpigotMob> {

    @Override
    protected UUID getEntityUUID(LivingEntity livingEntity) {
        return livingEntity.getUniqueId();
    }

    @Override
    protected boolean isPlayerControlledEntity(LivingEntity livingEntity) {
        return livingEntity.getType() == EntityType.PLAYER;
    }

    @Override
    protected IMob createEntity(LivingEntity entity) {
        String dimmName = entity.getLocation().getWorld().getName();
        SpigotMob iEntity = new SpigotMob(entity);
        initializeEntity(iEntity, dimmName, entity.getType().name());
        return iEntity;
    }

    @Override
    public void updateWalkSpeed(IEntity<? extends LivingEntity> entity) {
        double speed = getEntityProperty(entity, CommonProperties.walk_speed);

        LivingEntity le = entity.getEntity();
        AttributeInstance attribute = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        attribute.setBaseValue(speed);

        if (Rpg.get().getPluginConfig().DEBUG.isBalance()) {
            info(entity + " setting walk speed to " + speed);
        }
    }

    public double getMobDamage(LivingEntity entity) {
        return getMobDamage(entity.getLocation().getWorld().getName(), entity.getName());
    }

}
