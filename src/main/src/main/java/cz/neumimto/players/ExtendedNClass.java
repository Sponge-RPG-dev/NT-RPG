package cz.neumimto.players;

import cz.neumimto.players.groups.NClass;

/**
 * Created by NeumimTo on 28.7.2015.
 */
public class ExtendedNClass {
    public static ExtendedNClass Default = new ExtendedNClass() {{
        setnClass(NClass.Default);
        setPrimary(true);
    }};
    private NClass nClass;
    private double experiences;
    private boolean isPrimary;
    private int level;

    public NClass getnClass() {
        return nClass;
    }

    public void setnClass(NClass nClass) {
        this.nClass = nClass;
    }

    public double getExperiences() {
        return experiences;
    }

    public void setExperiences(double experiences) {
        this.experiences = experiences;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public int getLevel() {
        return level;
    }
}
