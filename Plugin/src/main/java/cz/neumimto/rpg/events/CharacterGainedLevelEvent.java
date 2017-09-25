package cz.neumimto.rpg.events;

import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 27.12.2015.
 */
public class CharacterGainedLevelEvent extends CharacterEvent {
	private final ExtendedNClass aClass;
	private final int level;
	private int skillpointsPerLevel;
	private int attributepointsPerLevel;

	public CharacterGainedLevelEvent(IActiveCharacter character, ExtendedNClass aClass, int level, int skillpointsPerLevel, int attributepointsPerLevel) {
		super(character);
		this.aClass = aClass;
		this.level = level;
		this.skillpointsPerLevel = skillpointsPerLevel;
		this.attributepointsPerLevel = attributepointsPerLevel;
	}

	public ExtendedNClass getaClass() {
		return aClass;
	}

	public int getLevel() {
		return level;
	}

	public int getSkillpointsPerLevel() {
		return skillpointsPerLevel;
	}

	public void setSkillpointsPerLevel(int skillpointsPerLevel) {
		this.skillpointsPerLevel = skillpointsPerLevel;
	}

	public int getAttributepointsPerLevel() {
		return attributepointsPerLevel;
	}

	public void setAttributepointsPerLevel(int attributepointsPerLevel) {
		this.attributepointsPerLevel = attributepointsPerLevel;
	}
}
