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
}
