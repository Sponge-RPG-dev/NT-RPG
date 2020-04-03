package cz.neumimto.skills.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

@Generate(id = "name", description = "Disables vanilla autoheal behavior")
public class FeatherFall extends EffectBase<Void> {

    public static String name = "Feather fall";

    public FeatherFall(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
    }

}

