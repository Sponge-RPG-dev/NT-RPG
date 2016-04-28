package cz.neumito.rpg.rest;

import cz.neumimto.rpg.players.IActiveCharacter;


public class CharacterData {

    private int level;
    private String charname;
    private int skillpoints;
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
        data.skillpoints = character.getSkillPoints();
        data.charname = character.getName();
        data.classname = character.getPrimaryClass().getnClass().getName();
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

    public int getSkillpoints() {
        return skillpoints;
    }

    public void setSkillpoints(int skillpoints) {
        this.skillpoints = skillpoints;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
