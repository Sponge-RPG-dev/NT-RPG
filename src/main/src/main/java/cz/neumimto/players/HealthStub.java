package cz.neumimto.players;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public class HealthStub extends Health {
    public HealthStub(IActiveCharacter activeCharacter) {
        super(activeCharacter);
    }

    @Override
    public double getValue() {
        return 20;
    }

    @Override
    public void setValue(double f) {

    }
}
