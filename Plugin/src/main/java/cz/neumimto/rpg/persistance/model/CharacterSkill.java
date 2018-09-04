package cz.neumimto.rpg.persistance.model;

import cz.neumimto.rpg.players.CharacterBase;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_skill")
public class CharacterSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long skillId;

	@ManyToOne
	@JoinColumn(name = "character_id")
	private CharacterBase characterBase;

	private int level;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id", nullable = true)
	private CharacterClass fromClass;

	@Column(name = "catalog_id")
	private String catalogId;

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

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
}
