package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.ISkill;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public interface ISpigotEntity<T extends LivingEntity> extends IEffectConsumer, IEntity<T>, AutoCloseable {

    @Override
    T getEntity();

    @Override
    UUID getUUID();

    ISkill skillOrEffectDamageCause();

    ISpigotEntity setSkillOrEffectDamageCause(ISkill rpgElement);

    @Override
    default void close() {
        setSkillOrEffectDamageCause(null);
    }

}
