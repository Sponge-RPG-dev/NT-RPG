package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.EntityHealth;
import cz.neumimto.rpg.players.Health;
import cz.neumimto.rpg.players.IEntityHealth;
import cz.neumimto.rpg.players.IReservable;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity<T extends Living> extends IEffectConsumer<T> {

    default double getHp() {
        return getHealth().getValue();
    }

    default void setHp(double d) {
        getHealth().setValue(d);
    }

    IEntityType getType();

    IEntityHealth getHealth();
}
