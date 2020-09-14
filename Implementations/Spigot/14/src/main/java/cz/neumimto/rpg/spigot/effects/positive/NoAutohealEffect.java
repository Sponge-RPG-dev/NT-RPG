package cz.neumimto.rpg.spigot.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

@Generate(id = "name", description = "Disables vanilla autoheal behavior")
public class NoAutohealEffect extends EffectBase<Void> {

    public static String name = "No Natural Healing";

    public NoAutohealEffect(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
    }

}
