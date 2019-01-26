package cz.neumimto.rpg.players.groups;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public interface ILevelProgression {

    double[] getLevelMargins();

    void setLevelMargins(double[] levelMargins);

    int getMaxLevel();

    double[] initCurve();
}
