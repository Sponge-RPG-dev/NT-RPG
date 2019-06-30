package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_class")
public class JPACharacterClass extends JPATimestampEntity implements CharacterClass {

    @Id
    @GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "class_id")
    private Long classId;

    @ManyToOne(targetEntity = JPACharacterBase.class)
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

    @Override
    public Long getId() {
        return classId;
    }

    @Override
    public void setId(Long id) {
        this.classId = id;
    }

    @Override
    public CharacterBase getCharacterBase() {
        return characterBase;
    }

    @Override
    public void setCharacterBase(CharacterBase characterBase) {
        this.characterBase = characterBase;
    }

    @Override
    public double getExperiences() {
        return experiences;
    }

    @Override
    public void setExperiences(double experiences) {
        this.experiences = experiences;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    @Override
    public int getUsedSkillPoints() {
        return usedSkillPoints;
    }

    @Override
    public void setUsedSkillPoints(int usedSkillPoints) {
        this.usedSkillPoints = usedSkillPoints;
    }


    @Override
    public int getLevel() {
        return level;
    }

    @Override
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

        JPACharacterClass that = (JPACharacterClass) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return classId != null ? classId.hashCode() : name.hashCode();
    }


}
