package cz.neumimto.rpg.players.leveling;

import cz.neumimto.rpg.players.groups.ILevelProgression;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public abstract class AbstractLevelprogression implements ILevelProgression {

    @Setting("MaxLevel")
    protected int maxLevel;

    protected double[] levelMargins;

    @Override
    public double[] getLevelMargins() {
        return levelMargins;
    }

    @Override
    public void setLevelMargins(double[] levelMargins) {
        this.levelMargins = levelMargins;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}
