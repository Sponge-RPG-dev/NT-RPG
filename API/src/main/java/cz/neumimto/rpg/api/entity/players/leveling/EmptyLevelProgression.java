package cz.neumimto.rpg.api.entity.players.leveling;

public class EmptyLevelProgression implements ILevelProgression {
    @Override
    public double[] getLevelMargins() {
        return new double[0];
    }

    @Override
    public void setLevelMargins(double[] levelMargins) {
        //Just no levels
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public double[] initCurve() {
        throw new RuntimeException("Operation Not Supported");
    }
}
