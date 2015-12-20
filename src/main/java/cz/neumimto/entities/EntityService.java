package cz.neumimto.entities;

import cz.neumimto.IEntity;
import cz.neumimto.IEntityType;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.players.CharacterService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import java.util.*;

/**
 * Created by NeumimTo on 19.12.2015.
 */
@Singleton
public class EntityService {

    private HashMap<UUID,IMob> entityHashMap = new HashMap<>();
    private Map<EntityType, Double> entityDamages = new HashMap<>();
    private Map<EntityType, Double> entityHealth = new HashMap<>();
    private Map<EntityType, Double> entityExperiences = new HashMap<>();

    @Inject
    private CharacterService service;

    @Inject
    private MobSettingsDao dao;

    public IEntity get(Living id) {
        if (id.getType() == EntityTypes.PLAYER) {
            return service.getCharacter(id.getUniqueId());
        }
        IMob iEntity = entityHashMap.get(id.getUniqueId());
        if (iEntity == null) {
            iEntity = new NEntity();
            iEntity.setExperiences(-1);
            entityHashMap.put(id.getUniqueId(),iEntity);
            if (!PluginConfig.OVERRIDE_MOBS) {
                id.offer(Keys.MAX_HEALTH,entityHealth.get(id.getType()));
            }
        }
        return iEntity;
    }

    public void remove(UUID e) {
        if (entityHashMap.containsKey(e))
            entityHashMap.remove(e);
    }

    public void remove(Collection<Entity> l) {
        for (Entity a : l) {
            UUID uniqueId = a.getUniqueId();
            remove(uniqueId);
        }
    }

    public double getMobDamage(IEntityType type) {
        return entityDamages.get(type);
    }

    public double getMobHealth(IEntityType type) {
        return entityHealth.get(type);
    }

    @PostProcess(priority = 10)
    public void load() {
        if (!PluginConfig.OVERRIDE_MOBS) {
            this.entityDamages.putAll(dao.getDamages());
            this.entityHealth.putAll(dao.getHealth());
            this.entityExperiences.putAll(dao.getExperiences());
        }
    }
}
