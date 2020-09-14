package cz.neumimto.rpg.api.entity;

/**
 * Represents any active resource, which can have current value, max value and regen.
 * Examples: Health, Mana, Stamina
 */
public interface IEntityResource {

    double getMaxValue();

    void setMaxValue(double f);

    double getValue();

    void setValue(double f);

    double getRegen();

    void setRegen(float f);

}
