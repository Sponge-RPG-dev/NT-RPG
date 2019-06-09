package cz.neumimto.rpg.sponge.entities;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.*;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.skill.SkillHealEvent;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.sponge.entities.configuration.SpongeMobSettingsDao;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

import static cz.neumimto.rpg.api.logging.Log.info;
import static cz.neumimto.rpg.api.logging.Log.warn;
import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 19.12.2015.
 */
@Singleton
public class SpongeEntityService implements EntityService<Living> {

    private HashMap<UUID, SpongeMob> entityHashMap = new HashMap<>();

    @Inject
    private SpongeMobSettingsDao dao;

    @Inject
    private PropertyService spongePropertyService;

    @Inject
    private CharacterService<IActiveCharacter> characterService;

    @Inject
    private EffectService effectService;


    @Override
    public IEntity get(Living id) {
        if (id.getType() == EntityTypes.PLAYER) {
            return characterService.getCharacter(id.getUniqueId());
        }
        IMob iEntity = entityHashMap.get(id.getUniqueId());
        if (iEntity == null) {
            iEntity = createEntity(id);
        }
        return iEntity;
    }

    public IEntity get(Entity id) {
        return get((Living)id);
    }

    private IMob createEntity(Living entity) {
        SpongeMob iEntity = new SpongeMob(entity);
        iEntity.setExperiences(-1);
        entityHashMap.put(entity.getUniqueId(), iEntity);
        MobsConfig dimmension = dao.getCache().getDimmension(entity.getLocation().getExtent().getName());
        if (!pluginConfig.OVERRIDE_MOBS && dimmension != null) {
            Double aDouble = dimmension.getHealth().get(entity.getType().getId());
            if (aDouble == null) {
                warn("No max health configured for " + entity.getType().getId() + " in world " + entity.getLocation().getExtent().getName());
            } else {
                entity.offer(Keys.MAX_HEALTH, aDouble);
                entity.offer(Keys.HEALTH, aDouble);
            }
        }
        return iEntity;
    }

    @Override
    public void remove(Living e) {
        UUID uuid = e.getUniqueId();
        if (entityHashMap.containsKey(uuid)) {
            IMob iMob = entityHashMap.get(uuid);
            effectService.removeAllEffects(iMob);
            entityHashMap.remove(uuid);
            iMob.detach();
        }
    }

    @Override
    public double getMobDamage(String dimension, String type) {
        MobsConfig dimmension = dao.getCache().getDimmension(dimension);
        if (dimmension != null) {
            Double aDouble = dimmension.getDamage().get(type);
            if (aDouble == null) {
                warn("No damage configured for " + type
                        + " in world " + dimension);
                aDouble = 0D;
            }
            return aDouble;
        }
        return 0;
    }

    public double getMobDamage(Living entity) {
        return getMobDamage(entity.getWorld().getName(), entity.getType().getId());
    }

    public double getExperiences(Living entity) {
        return getExperiences(entity.getWorld().getName(), entity.getType().getId());
    }

    @Override
    public double getExperiences(String dimension, String type) {
        MobsConfig dimmension = dao.getCache().getDimmension(dimension);
        if (dimmension != null) {
            Double aDouble = dimmension.getExperiences().get(type);
            if (aDouble == null) {
                warn("No max experience drop configured for " + type
                        + " in world " + dimension);
                aDouble = 0D;
            }
            return aDouble;
        }
        return 0;
    }

    /**
     * Unlike {@link IEntity#getProperty} this method checks for maximal allowed value, defined in config file.
     *
     * @see PropertyService#loadMaximalServerPropertyValues(Path) ()
     */
    @Override
    public float getEntityProperty(IEffectConsumer entity, int id) {
        return Math.min(entity.getProperty(id), spongePropertyService.getMaxPropertyValue(id));
    }

    /**
     * Heals the entity and fire an event
     *
     * @param entity
     * @param amount
     * @return difference
     */
    @Override
    public double healEntity(IEntity entity, float amount, IRpgElement source) {
        if (entity.getHealth().getValue() == entity.getHealth().getMaxValue()) {
            return 0;
        }

        SkillHealEvent event = Rpg.get().getEventFactory().createEventInstance(SkillHealEvent.class);

        event.setSource(source);
        event.setAmount(amount);
        event.setEntity(entity);

        if (Rpg.get().postEvent(event)) {
            return 0;
        }

        if (event.getAmount() <= 0) {
            return 0;
        }

        return setEntityHealth(event.getEntity(), entity.getHealth().getValue() + event.getAmount());
    }

    @Override
    public void reload() {
        dao.load(null);
    }

    /**
     * Sets entity's hp to chosen amount.
     *
     * @param entity
     * @param value
     * @return difference
     */
    public double setEntityHealth(IEntity entity, double value) {
        if (value > entity.getHealth().getMaxValue()) {
            setEntityToFullHealth(entity);
            return entity.getHealth().getMaxValue() - entity.getHealth().getValue();
        }
        double diff = value - entity.getHealth().getValue();
        entity.getHealth().setValue(value);
        return diff;
    }

    /**
     * sets entity to its full health
     *
     * @param entityToFullHealth
     */
    public void setEntityToFullHealth(IEntity entityToFullHealth) {
        entityToFullHealth.getHealth().setValue(entityToFullHealth.getHealth().getMaxValue());
    }


    /**
     * Updates character walkspeed to match SpongeDefaultProperties.walk_speed property
     *
     * @param entity
     */
    @Override
    public void updateWalkSpeed(IEntity<Living> entity) {
        double speed = getEntityProperty(entity, SpongeDefaultProperties.walk_speed);
        entity.getEntity().offer(Keys.WALKING_SPEED, speed);
        if (pluginConfig.DEBUG.isBalance()) {
            info(entity + " setting walk speed to " + speed);
        }
    }
}
