package cz.neumimto.rpg.common.entity;


/**
 * Created by NeumimTo on 19.12.2015.
 */
public interface IMob<T> extends IEntity<T> {

    double getExperiences();

    void setExperiences(double exp);

    @Override
    default IEntityType getType() {
        return IEntityType.MOB;
    }

    void detach();

}
