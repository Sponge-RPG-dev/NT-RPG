package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

@Generate(id = "name", description = "Disables vanilla autoheal behavior")
public class FeatherFall extends EffectBase<Void> {

    public static String name = "Feather fall";

    public FeatherFall(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
    }

}

