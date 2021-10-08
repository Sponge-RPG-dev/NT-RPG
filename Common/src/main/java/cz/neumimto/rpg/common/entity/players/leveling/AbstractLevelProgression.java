package cz.neumimto.rpg.common.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;

import java.util.Arrays;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public abstract class AbstractLevelProgression implements ILevelProgression {

    @PreserveNotNull
    @Path("MaxLevel")
    protected int maxLevel;

    protected transient double[] levelMargins;

    protected transient double totalExpAmount;

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

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }
}
