package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.api.effects.IEffect;

public interface ManaBar {
    String name = "ManaBar";
    void notifyManaChange();
    IEffect asEffect();
}
