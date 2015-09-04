package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SkillLearnEvent extends CancellableEvent {
    IActiveCharacter character;
    ISkill skill;

    public SkillLearnEvent(IActiveCharacter character, ISkill skill) {
        this.character = character;
        this.skill = skill;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public ISkill getSkill() {
        return skill;
    }
}
