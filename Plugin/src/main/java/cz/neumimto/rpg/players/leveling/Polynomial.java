package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public class Polynomial extends AbstractLevelprogression {

    @Setting("Mult")
    private double mult;

    @Setting("Factor")
    private double factor;

    @Override
    public double[] initCurve() {
        double[] arr = new double[getMaxLevel()];
        for (int i = 1; i < getMaxLevel()+1; i++) {
            arr[i] = mult * Math.pow(i, factor);
            System.out.println(arr[i]);
        }
        return arr;
    }

}
