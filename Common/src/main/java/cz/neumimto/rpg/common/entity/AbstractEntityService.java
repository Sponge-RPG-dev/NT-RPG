package cz.neumimto.rpg.common.entity;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.MobsConfig;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.events.skill.SkillHealEvent;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;

import static cz.neumimto.rpg.common.logging.Log.warn;

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
    public double getEntityProperty(IEffectConsumer entity, int id) {
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
    public double healEntity(IEntity entity, double amount, IRpgElement source) {
        Resource health = entity.getResource(ResourceService.health);
        if (health.getValue() == health.getMaxValue()) {
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

        Resource resource = event.getEntity().getResource(ResourceService.health);

        double value = health.getValue() + event.getAmount();

        if (value > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
            return resource.getMaxValue() - resource.getValue();
        }
        double diff = value - resource.getValue();
        resource.setValue(value);

        return diff;
    }

    @Override
    public double getExperiences(String dimension, String type, UUID uuid) {
        return entityHandler.getExperiences(dao, dimension, type, uuid);
    }

    protected void initializeEntity(I iEntity, String dimmName, String type) {
        iEntity.setExperiences(-1);
        entityHashMap.put(iEntity.getUUID(), iEntity);
    }

    public EntityHandler<I> getEntityHandler() {
        return entityHandler;
    }

    public void setEntityHandler(EntityHandler<I> entityHandler) {
        this.entityHandler = entityHandler;
    }

    public static class EntityHandler<I extends IEntity> {

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

    }

}
