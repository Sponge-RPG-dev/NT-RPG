package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.common.effects.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.skills.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which gives +x bonus points to all skill")
public class AllSkillsBonus extends EffectBase<Integer> {

    public static final String name = "All skill";

    public AllSkillsBonus(IEffectConsumer character, long duration, int value) {
        super(name, character);
        setDuration(duration);
        setStackable(true, IntegerEffectStackingStrategy.INSTANCE);
        setValue(value);
    }

    @Override
    public void onApply(IEffect self) {
        getConsumer().setProperty(CommonProperties.all_skills_bonus, getConsumer().getProperty(CommonProperties.all_skills_bonus) + getValue());
    }

    @Override
    public void onRemove(IEffect self) {
        getConsumer().setProperty(CommonProperties.all_skills_bonus, getConsumer().getProperty(CommonProperties.all_skills_bonus) - getValue());
    }

}
