package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

public class UiResourceTracker {

    public final boolean isEffectBased;

    public UiResourceTracker(boolean isEffectBased) {
        this.isEffectBased = isEffectBased;
    }

    public IEffect getOrCreateEffect(IActiveCharacter character, Resource resource) {
        return null;
    }

    public void runCustomTask(IActiveCharacter character, Resource resource) {

    }
}
