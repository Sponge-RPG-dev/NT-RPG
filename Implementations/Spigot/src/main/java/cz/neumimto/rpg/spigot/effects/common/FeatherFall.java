package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;

@AutoService(IEffect.class)
@Generate(id = "name", description = "No damage from falling")
public class FeatherFall extends EffectBase<Void> {

    public static String name = "Feather fall";

    @ScriptMeta.Handler
    public FeatherFall(@ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
                       @ScriptMeta.NamedParam("d|duration") long duration) {
        super(name, consumer);
        setDuration(duration);
    }

}

