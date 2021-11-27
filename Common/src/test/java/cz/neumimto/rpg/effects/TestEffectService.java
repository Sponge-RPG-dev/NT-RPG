package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;

import java.util.Set;

public class TestEffectService extends EffectService {

    @Override
    public void load() {

    }

    @Override
    public void startEffectScheduler() {

    }

    @Override
    protected boolean mayTick(IEffect e) {
        return e.getConsumer() != null;
    }

    @Override
    public void stopEffectScheduler() {

    }

    public Set<IEffect> getEffects() {
        return effectSet;
    }
}
