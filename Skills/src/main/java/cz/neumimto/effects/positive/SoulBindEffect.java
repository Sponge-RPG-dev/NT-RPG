package cz.neumimto.effects.positive;

import cz.neumimto.EffectLocalization;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 6.2.2016.
 */
public class SoulBindEffect extends EffectBase {

    private final IEffectConsumer target;

    public SoulBindEffect(IEffectConsumer caster, IEffectConsumer target) {
        this.target = target;
        setConsumer(caster);
        setExpireMessage(EffectLocalization.SOULBIND_EXPIRE);
    }

    public IEffectConsumer getTarget() {
        return target;
    }




}
