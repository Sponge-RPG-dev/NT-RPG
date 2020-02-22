package cz.neumimto.rpg.api.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public class Exponential extends AbstractLevelProgression {

    @Path("Sequence")
    private double sequence;

    @Path("Factor")
    private double factor;

    public double getSequence() {
        return sequence;
    }

    public double getFactor() {
        return factor;
    }

    @Override
    public double[] initCurve() {
        double[] arr = new double[getMaxLevel()];
        arr[0] = sequence;
        for (int i = 1; i < getMaxLevel(); i++) {
            arr[i] = factor * arr[i - 1];
        }
        return arr;
    }
}
