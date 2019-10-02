package cz.neumimto.rpg.api.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.Arrays;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public abstract class AbstractLevelProgression implements ILevelProgression {

    @Path("MaxLevel")
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
