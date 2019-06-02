package cz.neumimto.rpg.api.entity;

import java.util.UUID;

/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IMob extends IEntity {

    double getExperiences();

    void setExperiences(double exp);

    @Override
    default IEntityType getType() {
        return IEntityType.MOB;
    }

    void attach(UUID objectId, UUID extend, IReservable health);

    void detach();

}
