package cz.neumimto.rpg.api.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;

/**
 * Created by NeumimTo on 27.1.2019.
 */
public class Linear extends AbstractLevelProgression {

    @Path("Sequence")
    private double sequence;

    @Path("FirstLevelExp")
    private double firstLevel;

    @Override
    public double[] initCurve() {
        double[] arr = new double[getMaxLevel()];
        for (int i = 0; i < getMaxLevel(); i++) {
            arr[i] = firstLevel + sequence * i;
        }
        return arr;
    }
}
