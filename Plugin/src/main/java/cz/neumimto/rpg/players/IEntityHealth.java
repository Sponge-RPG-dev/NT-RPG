package cz.neumimto.rpg.players;

/**
 * Created by ja on 18.6.2017.
 */
public interface IEntityHealth {

	double getMaxValue();

	void setMaxValue(double f);

	double getValue();

	void setValue(double f);

	double getRegen();

	void setRegen(float f);
}
