package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public interface ISpigorEntity<T extends LivingEntity> extends IEffectConsumer, IEntity<T> {

    T getEntity();

    UUID getUUID();

}
