package cz.neumimto.rpg.persistance.model;

import cz.neumimto.rpg.players.CharacterBase;

import javax.persistence.*;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_skill")
public class CharacterSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long skillId;

	@ManyToOne
	@JoinColumn(name = "characterId")
	private CharacterBase characterBase;

	private String name;

	private int level;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "classId", nullable = true)
	private CharacterClass fromClass;

	public Long getId() {
		return skillId;
	}

	public void setId(Long id) {
		this.skillId = id;
	}

	public CharacterBase getCharacterBase() {
		return characterBase;
	}

	public void setCharacterBase(CharacterBase characterBase) {
		this.characterBase = characterBase;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public CharacterClass getFromClass() {
		return fromClass;
	}

	public void setFromClass(CharacterClass fromClass) {
		this.fromClass = fromClass;
	}

}
