package cz.neumimto.rpg.inventory.runewords;

/**
 * Created by NeumimTo on 29.10.2015.
 */
public class Rune {
    private String name;
    private double spawnchance;

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
}
