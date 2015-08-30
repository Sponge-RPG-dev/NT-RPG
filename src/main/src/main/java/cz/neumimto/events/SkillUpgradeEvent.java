package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SkillUpgradeEvent extends CancellableEvent {
    IActiveCharacter character;
    ISkill skill;
    int level;

    public SkillUpgradeEvent(IActiveCharacter character, ISkill skill, int level) {
        this.character = character;
        this.skill = skill;
        this.level = level;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
