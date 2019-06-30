package cz.neumimto.rpg.api.entity;


/**
 * Created by NeumimTo on 29.3.17.
 */
public interface PropertyContainer {

    float getProperty(int index);

    void setProperty(int index, float value);

    default void addProperty(int index, float value) {
        setProperty(index, getProperty(index) + value);
    }
}
