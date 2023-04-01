package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpigotMob extends AbstractMob<LivingEntity> implements ISpigotEntity<LivingEntity> {

    private LivingEntity entity;
    private ISkill soedc;

    private Map<String, Resource> resourceMap = new HashMap<>();

    public SpigotMob(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Resource getResource(String resource) {
        if (!resourceMap.containsKey(resource)) {
            resourceMap.put(resource, Rpg.get().getResourceService().initializeForAi(this));
        }
        return resourceMap.get(resource);
    }

    @Override
    public boolean isFriendlyTo(ActiveCharacter characterr) {
        if (!(getEntity() instanceof Tameable)) {
            return false;
        }
        Tameable t = (Tameable) getEntity();
        AnimalTamer owner = t.getOwner();
        if (owner == null) {
            return false;
        }


        SpigotCharacter character = (SpigotCharacter) characterr;
        if (owner.getUniqueId().equals(character.getUUID())) {
            return true;
        }


        IParty<SpigotCharacter> party = character.getParty();

        for (SpigotCharacter ActiveCharacter : party.getPlayers()) {
            UUID uniqueId = ActiveCharacter.getEntity().getUniqueId();
            if (owner.getUniqueId().equals(uniqueId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void detach() {
        this.entity = null;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public UUID getUUID() {
        return entity.getUniqueId();
    }

    @Override
    public ISkill skillOrEffectDamageCause() {
        return soedc;
    }

    @Override
    public ISpigotEntity setSkillOrEffectDamageCause(ISkill rpgElement) {
        this.soedc = rpgElement;
        return this;
    }

}
