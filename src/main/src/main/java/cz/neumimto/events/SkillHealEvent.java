package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 7.8.2015.
 */
public class SkillHealEvent extends CancellableEvent {
    private IActiveCharacter character;
    private double amount;

    public SkillHealEvent(IActiveCharacter character, double amount) {
        this.character = character;
        this.amount = amount;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
