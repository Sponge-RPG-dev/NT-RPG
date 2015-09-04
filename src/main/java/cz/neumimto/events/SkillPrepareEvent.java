package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public class SkillPrepareEvent extends CancellableEvent {
    private IActiveCharacter character;
    private float requiredHp;
    private float requiredMana;

    public SkillPrepareEvent(IActiveCharacter character, float requiredHp, float requiredMana) {
        this.character = character;
        this.requiredHp = requiredHp;

        this.requiredMana = requiredMana;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public float getRequiredHp() {
        return requiredHp;
    }

    public void setRequiredHp(float requiredHp) {
        this.requiredHp = requiredHp;
    }

    public float getRequiredMana() {
        return requiredMana;
    }

    public void setRequiredMana(float requiredMana) {
        this.requiredMana = requiredMana;
    }
}
