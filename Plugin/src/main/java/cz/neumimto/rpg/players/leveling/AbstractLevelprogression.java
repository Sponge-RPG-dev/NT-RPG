package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;

/**
 * Created by NeumimTo on 26.1.2019.
 */
@ConfigSerializable
public abstract class AbstractLevelprogression implements ILevelProgression {

    @Setting("MaxLevel")
    protected int maxLevel;

    protected double[] levelMargins;

    protected double totalExpAmount;

    @Override
    public double[] getLevelMargins() {
        return levelMargins;
    }

    @Override
    public void setLevelMargins(double[] levelMargins) {
        this.levelMargins = levelMargins;
        if (levelMargins != null) {
            totalExpAmount = Arrays.stream(levelMargins).sum();
        }
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}
