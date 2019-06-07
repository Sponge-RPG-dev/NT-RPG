package cz.neumimto.rpg.api.entity;


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

    void detach();

}
