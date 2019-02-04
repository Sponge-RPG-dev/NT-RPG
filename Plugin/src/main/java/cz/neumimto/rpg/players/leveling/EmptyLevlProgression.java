package cz.neumimto.rpg.players.leveling;

public class EmptyLevlProgression implements ILevelProgression {
    @Override
    public double[] getLevelMargins() {
        return new double[0];
    }

    @Override
    public void setLevelMargins(double[] levelMargins) {

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
