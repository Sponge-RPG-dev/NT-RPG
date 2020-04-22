package cz.neumimto.rpg.sponge.entities;

import static cz.neumimto.rpg.api.logging.Log.info;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IMob;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import java.util.UUID;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 19.12.2015.
 */
@Singleton
public class SpongeEntityService extends AbstractEntityService<Living, SpongeMob> {

    public IEntity get(Entity id) {
        return get((Living) id);
    }

    @Override
    protected UUID getEntityUUID(Living living) {
        return living.getUniqueId();
    }

    @Override
    protected boolean isPlayerControlledEntity(Living living) {
        return living.getType() == EntityTypes.PLAYER;
    }

    @Override
    protected IMob createEntity(Living entity) {
        SpongeMob iEntity = new SpongeMob(entity);
        initializeEntity(iEntity, entity.getLocation().getExtent().getName(), entity.getType().getId());
        return iEntity;
    }

    public double getMobDamage(Living entity) {
        return getMobDamage(entity.getWorld().getName(), entity.getType().getId());
    }

    public double getExperiences(Living entity) {
        return getExperiences(entity.getWorld().getName(), entity.getType().getId(), entity.getUniqueId());
    }

    /**
     * Updates character walkspeed to match SpongeDefaultProperties.walk_speed property
     *
     * @param entity
     */
    @Override
    public void updateWalkSpeed(IEntity<? extends Living> entity) {
        double speed = getEntityProperty(entity, CommonProperties.walk_speed);
        entity.getEntity().offer(Keys.WALKING_SPEED, speed);
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.DEBUG.isBalance()) {
            info(entity + " setting walk speed to " + speed);
        }
    }

}
