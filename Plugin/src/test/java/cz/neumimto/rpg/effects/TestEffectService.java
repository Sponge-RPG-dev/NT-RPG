package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.common.effects.EffectService;

import java.util.Set;

public class TestEffectService extends EffectService {
    @Override
    public void startEffectScheduler() {

    }

    @Override
    public void stopEffectScheduler() {

    }

    public Set<IEffect> getEffects() {
        return effectSet;
    }
}
