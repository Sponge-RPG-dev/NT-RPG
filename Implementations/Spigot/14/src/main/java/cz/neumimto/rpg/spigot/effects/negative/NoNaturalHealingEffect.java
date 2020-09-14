package cz.neumimto.rpg.spigot.effects.negative;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

@Generate(id = "name", description = "Entity cannot be healed by potions, food")
public class NoNaturalHealingEffect extends EffectBase<NoNaturalHealingEffect.Model> {

    public static String name = "No Natural Healing";

    public NoNaturalHealingEffect(IEffectConsumer consumer, Model model) {
        super(name, consumer);
        setDuration(model.duration);
        setValue(model);
    }

    public static class Model {
        public long duration;
    }
}
