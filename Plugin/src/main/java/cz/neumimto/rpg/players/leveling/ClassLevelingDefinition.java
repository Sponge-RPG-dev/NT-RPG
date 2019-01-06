package cz.neumimto.rpg.players.leveling;

import java.math.BigDecimal;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class ClassLevelingDefinition {

	private LevelHandler handler;

	private double[] levelMargins;

	private BigDecimal totalExp;



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

	public BigDecimal getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(BigDecimal totalExp) {
		this.totalExp = totalExp;
	}
}
