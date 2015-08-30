package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SkillPostUsageEvent extends CancellableEvent {
    private IActiveCharacter character;
    private double hpcost;
    private double manacost;
    private long cooldown;

    public SkillPostUsageEvent(IActiveCharacter character, double hpcost, double manacost, long cooldown) {
        this.character = character;
        this.hpcost = hpcost;
        this.manacost = manacost;
        this.cooldown = cooldown;
    }


    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public double getHpcost() {
        return hpcost;
    }

    public void setHpcost(float hpcost) {
        this.hpcost = hpcost;
    }

    public double getManacost() {
        return manacost;
    }

    public void setManacost(float manacost) {
        this.manacost = manacost;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
