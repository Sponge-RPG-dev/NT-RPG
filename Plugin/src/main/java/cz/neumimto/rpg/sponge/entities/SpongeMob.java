package cz.neumimto.rpg.sponge.entities;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntityResource;
import cz.neumimto.rpg.api.entity.IMob;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public class SpongeMob implements ISpongeEntity<Living>, IMob<Living> {

    private double experiences;
    private Living entity;
    private Map<String, IEffectContainer<Object, IEffect<Object>>> effectSet = new HashMap<>();
    private Map<Integer, Float> properties = new HashMap<>();
    private IEntityResource entityHealth;

    public SpongeMob(Living entity) {
        this.entity = entity;
    }

    @Override
    public double getExperiences() {
        return experiences;
    }

    @Override
    public void setExperiences(double exp) {
        this.experiences = exp;
    }

    @Override
    public IEntityResource getHealth() {
        return entityHealth;
    }

    //todo remove casts
    @Override
    public boolean isFriendlyTo(IActiveCharacter characterr) {
        Optional<Optional<UUID>> uuid = getEntity().get(Keys.TAMED_OWNER);
        if (uuid.isPresent()) {
            ISpongeCharacter character = (ISpongeCharacter) characterr;
            Optional<UUID> uuid1 = uuid.get();
            if (uuid1.isPresent()) {
                UUID uuid2 = uuid1.get();
                if (character.getPlayer().getUniqueId().equals(uuid2)) {
                    return true;
                }
                IParty<ISpongeCharacter> party = character.getParty();
                for (ISpongeCharacter iActiveCharacter : party.getPlayers()) {
                    UUID uniqueId = iActiveCharacter.getPlayer().getUniqueId();
                    if (uuid2.equals(uniqueId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void detach() {
        this.entityHealth = null;
        this.entity = null;
    }

    public Living getEntity() {
        return entity;
    }

    @Override
    public UUID getUUID() {
        return entity.getUniqueId();
    }

    @Override
    public boolean isDetached() {
        return entity == null;
    }

    @Override
    public Map<String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return effectSet;
    }

    @Override
    public float getProperty(int propertyName) {
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        }
        return NtRpgPlugin.GlobalScope.spongePropertyService.getDefault(propertyName);
    }

    @Override
    public void setProperty(int propertyName, float value) {
        properties.put(propertyName, value);
    }

}
