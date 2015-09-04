package cz.neumimto.players;

/**
 * Created by NeumimTo on 30.12.2014.
 */
public interface IReservable {
    double getMaxValue();

    void setMaxValue(double f);

    void setReservedAmnout(float f);

    double getReservedAmount();

    double getValue();

    void setValue(double f);

    double getRegen();

    void setRegen(float f);
}
