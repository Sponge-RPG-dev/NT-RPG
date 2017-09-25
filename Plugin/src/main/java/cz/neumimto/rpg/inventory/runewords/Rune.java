package cz.neumimto.rpg.inventory.runewords;

import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune {
	private String name;
	private double spawnchance;
	private Text lore;

	public Rune() {
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public double getSpawnchance() {
		return spawnchance;
	}

	public void setSpawnchance(double spawnchance) {
		this.spawnchance = spawnchance;
	}

	public void setLore(String text) {
		if (text == null)
			text = " ";
		lore = Text.of(text);
	}

	public Text getLore() {
		return lore;
	}
}
