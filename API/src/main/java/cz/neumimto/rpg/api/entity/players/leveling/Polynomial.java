package cz.neumimto.rpg.api.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public class Polynomial extends AbstractLevelProgression {

    @Path("Mult")
    private double mult;

    @Path("Factor")
    private double factor;

    @Override
    public double[] initCurve() {
        double[] arr = new double[getMaxLevel()];
        for (int i = 1; i < getMaxLevel() + 1; i++) {
            arr[i] = mult * Math.pow(i, factor);
        }
        return arr;
    }

}
