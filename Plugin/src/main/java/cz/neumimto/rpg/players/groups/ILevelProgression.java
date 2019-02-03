package cz.neumimto.rpg.players.groups;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public interface ILevelProgression {

    double[] getLevelMargins();

    void setLevelMargins(double[] levelMargins);

    int getMaxLevel();

    double[] initCurve();


    ILevelProgression EMPTY = new ILevelProgression() {
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
    };
}
