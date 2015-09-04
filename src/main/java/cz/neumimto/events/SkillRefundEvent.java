package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;

/**
 * Created by NeumimTo on 27.7.2015.
 */
public class SkillRefundEvent extends CancellableEvent {
    private IActiveCharacter character;
    private ISkill skill;

    public SkillRefundEvent(IActiveCharacter character, ISkill skill) {
        super();
        this.character = character;
        this.skill = skill;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public ISkill getSkill() {
        return skill;
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }
}
