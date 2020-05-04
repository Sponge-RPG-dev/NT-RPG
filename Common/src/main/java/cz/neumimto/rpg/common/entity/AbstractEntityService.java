package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.*;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.events.skill.SkillHealEvent;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;

import static cz.neumimto.rpg.api.logging.Log.warn;

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

    protected EntityHandler<I> entityHandler;

    public AbstractEntityService() {
        entityHashMap = new HashMap<>();
        setEntityHandler(new EntityHandler<>());
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
        remove(uuid);
    }

    @Override
    public void remove(UUID uuid) {
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
        return entityHandler.getMobDamage(dao, dimension, type);
    }

    @Override
    public boolean handleMobDamage(String dimension, UUID uuid) {
        return entityHandler.handleMobDamage(dimension, uuid);
    }

    @Override
    public double getExperiences(String dimension, String type, UUID uuid) {
        return entityHandler.getExperiences(dao, dimension, type, uuid);
    }

    protected void initializeEntity(I iEntity, String dimmName, String type) {
        iEntity.setExperiences(-1);
        iEntity = entityHandler.initializeEntity(dao, iEntity, dimmName, type);
        entityHashMap.put(iEntity.getUUID(), iEntity);
    }

    public EntityHandler<I> getEntityHandler() {
        return entityHandler;
    }

    public void setEntityHandler(EntityHandler<I> entityHandler) {
        this.entityHandler = entityHandler;
    }

    public static class EntityHandler<I extends IEntity> {

        public I initializeEntity(MobSettingsDao dao, I iEntity, String dimName, String type) {
            MobsConfig dimension = dao.getCache().getDimmension(dimName);
            if (!Rpg.get().getPluginConfig().OVERRIDE_MOBS && dimension != null) {
                Double aDouble = dimension.getHealth().get(type);
                if (aDouble == null) {
                    warn("No max health configured for " + type + " in world " + dimName);
                } else {
                    iEntity.getHealth().setMaxValue(aDouble);
                    iEntity.getHealth().setValue(aDouble);
                }
            }
            return iEntity;
        }

        public double getExperiences(MobSettingsDao dao, String dimName, String type, UUID uuid) {
            MobsConfig dimension = dao.getCache().getDimmension(dimName);
            if (dimension != null) {
                Double aDouble = dimension.getExperiences().get(type);
                if (aDouble == null) {
                    warn("No max experience drop configured for " + type + " in world " + dimName);
                    aDouble = 0D;
                }
                return aDouble;
            }
            return 0;
        }

        public double getMobDamage(MobSettingsDao dao, String dimName, String type) {
            MobsConfig dimension = dao.getCache().getDimmension(dimName);
            if (dimension != null) {
                Double aDouble = dimension.getDamage().get(type);
                if (aDouble == null) {
                    warn("No damage configured for " + type + " in world " + dimName);
                    aDouble = 0D;
                }
                return aDouble;
            }
            return 0;
        }

        public boolean handleMobDamage(String dimension, UUID uuid) {
            return true;
        }

    }

}
