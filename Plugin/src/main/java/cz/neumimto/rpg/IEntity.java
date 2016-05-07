package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IEntity<T extends Living> extends IEffectConsumer<T> {
    double getHp();
    void setHp(double d);
    IEntityType getType();
}
