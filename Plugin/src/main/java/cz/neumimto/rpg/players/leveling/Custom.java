package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public class Custom extends AbstractLevelprogression {

    @Setting("Levels")
    private double[] lvlMgrs;

    @Override
    public double[] initCurve() {
        return lvlMgrs;
    }

    @Override
    public double[] getLevelMargins() {
        return lvlMgrs;
    }

    @Override
    public void setLevelMargins(double[] levelMargins) {
        lvlMgrs = levelMargins;
    }
}
