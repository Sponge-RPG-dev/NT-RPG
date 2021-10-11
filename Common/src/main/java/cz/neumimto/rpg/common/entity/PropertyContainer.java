package cz.neumimto.rpg.common.entity;


/**
 * Created by NeumimTo on 29.3.17.
 */
public interface PropertyContainer {

    double getProperty(int index);

    void setProperty(int index, double value);

    default void addProperty(int index, float value) {
        setProperty(index, getProperty(index) + value);
    }
}
