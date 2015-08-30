package cz.neumimto.skills;

/**
 * Created by NeumimTo on 16.2.2015.
 */
public enum SkillNode {

    DAMAGE("damage"),
    RADIUS("radius"),
    MANACOST("manacost"),
    COOLDOWN("cooldown"),
    VELOCITY("velocity"),
    HPCOST("hpcost");


    private final String str;

    SkillNode(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

}
