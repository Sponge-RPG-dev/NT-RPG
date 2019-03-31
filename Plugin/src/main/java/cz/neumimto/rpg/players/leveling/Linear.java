package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 27.1.2019.
 */
@ConfigSerializable
public class Linear extends AbstractLevelProgression {

    @Setting("Sequence")
    private double sequence;

    @Setting("FirstLevelExp")
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
