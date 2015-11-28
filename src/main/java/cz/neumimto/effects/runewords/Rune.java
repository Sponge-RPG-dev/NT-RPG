package cz.neumimto.effects.runewords;

import cz.neumimto.effects.IGlobalEffect;

import java.util.Set;

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

    public void setName(String name) {
        this.name = name;
    }

    public double getSpawnchance() {
        return spawnchance;
    }

    public void setSpawnchance(double spawnchance) {
        this.spawnchance = spawnchance;
    }
}
