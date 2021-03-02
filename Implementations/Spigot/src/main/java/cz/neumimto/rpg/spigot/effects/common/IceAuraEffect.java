package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.effects.common.model.IceAuraEffectModel;

@Generate(id = "name", description = "")
public class IceAuraEffect extends EffectBase<IceAuraEffectModel> {

    public static final String name = "IceAura";

    public IceAuraEffect(IEffectConsumer consumer, IceAuraEffectModel model) {
        super(name, consumer);
    }

    @Override
    public void onApply(IEffect self) {

    }

    @Override
    public void onTick(IEffect self) {

    }

    @Override
    public void onRemove(IEffect self) {

    }
}
