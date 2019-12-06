package cz.neumimto.rpg.api.entity.players.leveling;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.Arrays;

/**
 * Created by NeumimTo on 26.1.2019.
 */
public abstract class AbstractLevelProgression implements ILevelProgression {

    @Optional
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
