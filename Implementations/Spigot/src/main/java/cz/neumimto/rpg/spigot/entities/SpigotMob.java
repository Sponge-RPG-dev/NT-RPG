package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.party.IParty;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.util.UUID;

public class SpigotMob extends AbstractMob<LivingEntity> implements ISpigotEntity<LivingEntity> {

    private LivingEntity entity;
    private ISkill soedc;

    public SpigotMob(LivingEntity entity) {
        this.entity = entity;
        this.entityHealth = new SpigotEntityHealth(entity);
    }

    @Override
    public boolean isFriendlyTo(IActiveCharacter characterr) {
        if (!(getEntity() instanceof Tameable)) {
            return false;
        }
        Tameable t = (Tameable) getEntity();
        AnimalTamer owner = t.getOwner();
        if (owner == null) {
            return false;
        }


        ISpigotCharacter character = (ISpigotCharacter) characterr;
        if (owner.getUniqueId().equals(character.getUUID())) {
            return true;
        }


        IParty<ISpigotCharacter> party = character.getParty();

        for (ISpigotCharacter iActiveCharacter : party.getPlayers()) {
            UUID uniqueId = iActiveCharacter.getEntity().getUniqueId();
            if (owner.getUniqueId().equals(uniqueId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void detach() {
        this.entityHealth = null;
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
