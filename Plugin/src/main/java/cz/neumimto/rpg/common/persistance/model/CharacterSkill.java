package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.players.CharacterBase;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by ja on 8.10.2016.
 */
@Entity(name = "rpg_character_skill")
public class CharacterSkill {

    @Id
    @GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "skill_id")
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

    @Column(name = "cooldown")
    private Long cooldown;

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

    public Long getCooldown() {
        return cooldown;
    }

    public void setCooldown(Long cooldown) {
        this.cooldown = cooldown;
    }
}
