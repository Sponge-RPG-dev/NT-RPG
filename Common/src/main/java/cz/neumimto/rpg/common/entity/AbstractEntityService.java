package cz.neumimto.rpg.common.entity;

import static cz.neumimto.rpg.api.logging.Log.warn;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.*;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.events.skill.SkillHealEvent;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;

import java.util.HashMap;
import java.util.UUID;
import javax.inject.Inject;

public abstract class AbstractEntityService<T, I extends IMob<T>> implements EntityService<T> {

    private HashMap<UUID, I> entityHashMap;

    @Inject
    private CharacterService characterService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private EffectService effectService;

    @Inject
    protected MobSettingsDao dao;

    public AbstractEntityService() {
        entityHashMap = new HashMap<>();
    }

    protected abstract UUID getEntityUUID(T t);

    protected abstract boolean isPlayerControlledEntity(T t);

    protected abstract IMob createEntity(T entity);


    @Override
    public IEntity get(T entity) {
        if (isPlayerControlledEntity(entity)) {
            return characterService.getCharacter(getEntityUUID(entity));
        }
        IMob iEntity = entityHashMap.get(getEntityUUID(entity));
        if (iEntity == null) {
            iEntity = createEntity(entity);
        }
        return iEntity;
    }

    /**
     * Unlike {@link IEntity#getProperty} this method checks for maximal allowed value, defined in config file.
     *
     * @see PropertyService#loadMaximalServerPropertyValues()
     */
    @Override
    public float getEntityProperty(IEffectConsumer entity, int id) {
        return Math.min(entity.getProperty(id), propertyService.getMaxPropertyValue(id));
    }

    @Override
    public void remove(T e) {
        UUID uuid = getEntityUUID(e);
        if (entityHashMap.containsKey(uuid)) {
            IMob iMob = entityHashMap.get(uuid);
            effectService.removeAllEffects(iMob);
            entityHashMap.remove(uuid);
            iMob.detach();
        }
    }

    @Override
    public void reload() {
        dao.load();
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

    protected void initializeEntity(I iEntity, UUID uuid, String dimmName, String id) {
        iEntity.setExperiences(-1);
        MobsConfig dimmension = dao.getCache().getDimmension(dimmName);
        if (!Rpg.get().getPluginConfig().OVERRIDE_MOBS && dimmension != null) {
            Double aDouble = dimmension.getHealth().get(id);
            if (aDouble == null) {
                warn("No max health configured for " + id + " in world " + dimmName);
            } else {
                iEntity.getHealth().setMaxValue(aDouble);
                iEntity.getHealth().setValue(aDouble);
            }
        }
        entityHashMap.put(uuid, iEntity);
    }
}
