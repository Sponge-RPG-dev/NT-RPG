package cz.neumimto.rpg.spigot.effects.common.model;

public class SlowModel {
    public int slowLevel;
    public boolean decreasedJumpHeight;

    public SlowModel() {
    }

    public SlowModel(int slowLevel, boolean decreasedJumpHeight) {
        this.slowLevel = slowLevel;
        this.decreasedJumpHeight = decreasedJumpHeight;
    }
}
