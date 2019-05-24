package cz.neumimto.rpg.persistance.model;

import cz.neumimto.rpg.players.CharacterBase;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_class")
public class CharacterClass {

    @Id
    @GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "class_id")
    private Long classId;

    @ManyToOne
    @JoinColumn(name = "character_id")
    private CharacterBase characterBase;

    @Column(name = "experiences")
    private Double experiences;

    @Column(name = "level")
    private Integer level;

    @Column(name = "name")
    private String name;

    @Column(name = "used_skil_points")
    private Integer usedSkillPoints;

    @Column(name = "skillpoints")
    protected Integer skillPoints;

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

    public double getExperiences() {
        return experiences;
    }

    public void setExperiences(double experiences) {
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


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CharacterClass that = (CharacterClass) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return classId != null ? classId.hashCode() : name.hashCode();
    }


}
