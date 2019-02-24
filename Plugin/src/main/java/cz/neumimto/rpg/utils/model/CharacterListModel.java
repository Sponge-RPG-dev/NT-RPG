package cz.neumimto.rpg.utils.model;

/**
 * Created by ja on 8.10.2016.
 */
public class CharacterListModel {

	private String characterName;
	private String concatClassNames;
	private Integer primaryClassLevel;

	public CharacterListModel(String characterName, String concatClassNames, Integer primaryClassLevel) {
		this.characterName = characterName;
		this.concatClassNames = concatClassNames;
		this.primaryClassLevel = primaryClassLevel;
	}

	public String getCharacterName() {
		return characterName;
	}

	public String getConcatClassNames() {
		return concatClassNames;
	}

	public Integer getPrimaryClassLevel() {
		return primaryClassLevel;
	}

}
