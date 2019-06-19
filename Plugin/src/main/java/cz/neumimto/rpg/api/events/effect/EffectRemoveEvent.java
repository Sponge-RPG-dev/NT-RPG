package cz.neumimto.rpg.api.events.effect;


import cz.neumimto.rpg.api.effects.IEffect;

public interface EffectRemoveEvent<T extends IEffect> extends TargetEffectEvent<T> {

}
