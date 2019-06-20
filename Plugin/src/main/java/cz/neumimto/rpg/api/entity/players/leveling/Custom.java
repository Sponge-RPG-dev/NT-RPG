package cz.neumimto.rpg.api.entity.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public class Custom extends AbstractLevelProgression {

    @Setting("Levels")
    private List<Double> lvlMgrs;

    //because configurate and lightbend/config are extremly useless libraries once you stopE doing stupid hello fucking world applications.
    @Override
    public double[] initCurve() {
        double[] doubles = lvlMgrs.stream().mapToDouble(Double::doubleValue).toArray();
        lvlMgrs = null;
        return doubles;
    }

    @Override
    public int getMaxLevel() {
        return getLevelMargins().length;
    }
}
