package cz.neumimto.rpg.common.persistance.model;

public class BaseCharacterAttribute extends TimestampEntity {

    private Long id;
    private CharacterBase characterBase;
    private String name;
    private int level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseCharacterAttribute that = (BaseCharacterAttribute) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 1236411;
    }
}
