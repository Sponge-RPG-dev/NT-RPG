package cz.neumimto.rpg.common.skills;


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
    AMOUNT("amount"),
    PERIOD("period"),
    CHANCE("chance"),
    MULTIPLIER("multiplier"),
    HEALED_AMOUNT("healed_amount"),
    AMPLIFIER("amplifier"),
    MAX("max"),
    DISTANCE("distance"),
    GRAVITY("gravity");
    private final String str;

    SkillNodes(String str) {
        this.str = str;
    }

    @Override
    public String value() {
        return str;
    }


}
