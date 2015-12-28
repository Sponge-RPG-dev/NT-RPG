package cz.neumimto.entities;

import cz.neumimto.IEntity;
import cz.neumimto.IEntityType;
import cz.neumimto.effects.IEffectConsumer;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IMob<T extends Living>  extends IEntity{
    double getExperiences();
    void setExperiences(double exp);
    default IEntityType getType() {
        return IEntityType.MOB;
    }
    void attach(T t);
    void detach();
    boolean isDetached();
}
