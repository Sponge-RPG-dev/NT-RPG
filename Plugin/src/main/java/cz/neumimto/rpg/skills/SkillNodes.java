package cz.neumimto.rpg.skills;

/**
 * Created by ja on 22.10.2016.
 */
public enum SkillNodes implements ISkillNode {

    DAMAGE("damage"),
    RADIUS("radius"),
    MANACOST("manacost"),
    COOLDOWN("cooldown"),
    VELOCITY("velocity"),
    HPCOST("hpcost"),
    PROJECTILE_TYPE("projectile-type"),
    RANGE("range"),
    DURATION("duration"),
    AMOUNT("amount");


    private final String str;

    SkillNodes(String str) {
        this.str = str;
    }

    @Override
    public String stringValue() {
        return str;
    }


}
