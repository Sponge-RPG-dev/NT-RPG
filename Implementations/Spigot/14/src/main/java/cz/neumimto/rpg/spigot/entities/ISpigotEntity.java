package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public interface ISpigotEntity<T extends LivingEntity> extends IEffectConsumer, IEntity<T>, AutoCloseable {

    T getEntity();

    UUID getUUID();

    ISkill skillOrEffectDamageCayse();

    ISpigotEntity setSkillOrEffectDamageCause(ISkill rpgElement);

    @Override
    default void close() {
        setSkillOrEffectDamageCause(null);
    };

}
