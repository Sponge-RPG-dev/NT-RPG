package cz.neumito.rpg.rest;

import cz.neumimto.rpg.players.IActiveCharacter;


public class CharacterData {

	private int level;
	private String charname;
	private String classname;

	public CharacterData() {
	}

	public static CharacterData fromCharacter(IActiveCharacter character) {
		if (character.isStub()) {
			CharacterData data = new CharacterData();
			data.charname = "None";
			return data;
		}
		CharacterData data = new CharacterData();
		data.level = character.getLevel();
		data.charname = character.getName();
		data.classname = character.getPrimaryClass().getConfigClass().getName();
		return data;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCharname() {
		return charname;
	}

	public void setCharname(String charname) {
		this.charname = charname;
	}


	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}
}
