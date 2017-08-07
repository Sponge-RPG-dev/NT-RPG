package cz.neumimto.rpg.persistance.model;

import cz.neumimto.rpg.players.CharacterBase;

import javax.persistence.*;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_class")
public class CharacterClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @ManyToOne
    @JoinColumn(name = "characterId")
    private CharacterBase characterBase;

    @Column(name = "experiences")
    private Double experiences;

    private String name;

    @Column(name = "skillpoints")
    protected int skillPoints;

    @Column(name = "used_skil_points")
    private int usedSkillPoints;

    public Long getId() {
        return classId;
    }

    public void setId(Long id) {
        this.classId = id;
    }

    public CharacterBase getCharacterBase() {
        return characterBase;
    }

    public void setCharacterBase(CharacterBase characterBase) {
        this.characterBase = characterBase;
    }

    public Double getExperiences() {
        return experiences;
    }

    public void setExperiences(Double experiences) {
        this.experiences = experiences;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getUsedSkillPoints() {
        return usedSkillPoints;
    }

    public void setUsedSkillPoints(int usedSkillPoints) {
        this.usedSkillPoints = usedSkillPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterClass that = (CharacterClass) o;

        return classId.equals(that.classId);

    }

    @Override
    public int hashCode() {
        return classId != null ? classId.hashCode() : 775701;
    }


}
