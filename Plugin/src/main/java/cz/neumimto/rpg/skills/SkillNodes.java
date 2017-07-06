package cz.neumimto.rpg.skills;

/**
 * Created by ja on 22.10.2016.
 */
public enum SkillNodes implements ISkillNode {

    DAMAGE("damage", SkillModifierProcessor.F_STACK),
    RADIUS("radius", SkillModifierProcessor.D_STACK),
    MANACOST("manacost", SkillModifierProcessor.F_STACK),
    COOLDOWN("cooldown", SkillModifierProcessor.L_STACK),
    VELOCITY("velocity", SkillModifierProcessor.D_STACK),
    HPCOST("hpcost", SkillModifierProcessor.D_STACK),
    PROJECTILE_TYPE("projectile-type", null),
    RANGE("range", SkillModifierProcessor.D_STACK),
    DURATION("duration", SkillModifierProcessor.L_STACK),
    AMOUNT("amount", SkillModifierProcessor.D_STACK),
    PERIOD("period", SkillModifierProcessor.L_STACK),
    CHANCE("chance", SkillModifierProcessor.D_STACK ),
    MULTIPLIER("multiplier",SkillModifierProcessor.F_MAX);


    private final String str;
    private final SkillModifierProcessor processor;
    SkillNodes(String str, SkillModifierProcessor processor) {
        this.str = str;
        this.processor = processor;
    }

    @Override
    public String value() {
        return str;
    }

    @Override
    public SkillModifierProcessor duplicityProcessor() {
        return processor;
    }


}
