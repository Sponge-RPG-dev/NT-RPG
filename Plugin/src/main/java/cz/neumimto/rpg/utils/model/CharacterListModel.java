package cz.neumimto.rpg.utils.model;

/**
 * Created by ja on 8.10.2016.
 */
public class CharacterListModel {
    private String characterName;
    private String primaryClassName;
    private Double primaryClassExp;

    public CharacterListModel(String characterName, String primaryClassName, Double primaryClassExp) {
        this.characterName = characterName;
        this.primaryClassName = primaryClassName;
        this.primaryClassExp = primaryClassExp;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getPrimaryClassName() {
        return primaryClassName;
    }

    public void setPrimaryClassName(String primaryClassName) {
        this.primaryClassName = primaryClassName;
    }

    public Double getPrimaryClassExp() {
        return primaryClassExp;
    }

    public void setPrimaryClassExp(Double primaryClassExp) {
        this.primaryClassExp = primaryClassExp;
    }
}
