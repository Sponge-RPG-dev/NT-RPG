package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public class Exponential extends AbstractLevelprogression {

    @Setting("Sequence")
    private double sequence;

    @Setting("Factor")
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
            arr[i] = factor * arr[i-1];
        }
        return arr;
    }
}
