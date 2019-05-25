package cz.neumimto.rpg.entities;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.LocalizableParametrizedText;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
//todo use mixins
public class NEntity implements IMob {

    private double experiences;
    //	private WeakReference<Living> entity;
    private Map<String, IEffectContainer> effectSet = new HashMap<>();
    private Map<Integer, Float> properties = new HashMap<>();
    private EntityHealth entityHealth;
    private UUID uuid;
    private UUID extent;

    protected NEntity(Living l) {
        attach(l);
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
    public EntityHealth getHealth() {
        return entityHealth;
    }

    @Override
    public boolean isFriendlyTo(IActiveCharacter character) {
        Optional<Optional<UUID>> uuid = getEntity().get(Keys.TAMED_OWNER);
        if (uuid.isPresent()) {
            Optional<UUID> uuid1 = uuid.get();
            if (uuid1.isPresent()) {
                UUID uuid2 = uuid1.get();
                if (character.getPlayer().getUniqueId().equals(uuid2)) {
                    return true;
                }
                Party party = character.getParty();
                for (IActiveCharacter iActiveCharacter : party.getPlayers()) {
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
    public void attach(Living creature) {
        //this.entity = new WeakReference<>(creature);
        this.uuid = creature.getUniqueId();
        this.extent = creature.getWorld().getUniqueId();
        this.entityHealth = new EntityHealth(this);
    }

    @Override
    public void detach() {
        entityHealth = null;
        this.uuid = null;
        this.extent = null;
        //this.entity = null;
    }


    @Override
    public Living getEntity() {
        World world = Sponge.getServer().getWorld(extent).get();
        Entity entity = world.getEntity(uuid).get();
        return (Living) entity;
    }

    @Override
    public boolean isDetached() {
        return uuid == null || getEntity() == null;
    }

    @Override
    public Map<String, IEffectContainer> getEffectMap() {
        return effectSet;
    }

    @Override
    public void sendMessage(LocalizableParametrizedText message, Arg arg) {

    }

    @Override
    public void sendMessage(ChatType chatType, Text message) {

    }

    @Override
    public void sendMessage(Text t) {

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
