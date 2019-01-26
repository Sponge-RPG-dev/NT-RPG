package cz.neumimto.rpg.players.leveling;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by NeumimTo on 6.1.2019.
 */
@ConfigSerializable
public class ClassLevelingDefinition {

	private LevelHandler handler;

	private double[] levelMargins;

	private long totalExp;

	public LevelHandler getHandler() {
		return handler;
	}

	public void setHandler(LevelHandler handler) {
		this.handler = handler;
	}

	public double[] getLevelMargins() {
		return levelMargins;
	}

	public void setLevelMargins(double[] levelMargins) {
		this.levelMargins = levelMargins;
	}

	public long getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(long totalExp) {
		this.totalExp = totalExp;
	}
}
