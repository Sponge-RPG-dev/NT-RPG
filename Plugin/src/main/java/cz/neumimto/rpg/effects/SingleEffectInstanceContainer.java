package cz.neumimto.rpg.effects;

/**
 * Created by NeumimTo on 5.6.17.
 */
public class SingleEffectInstanceContainer<K, T extends IEffect<K>> extends EffectContainer<K,T> {

    public SingleEffectInstanceContainer(T t) {
        super(t);
    }

    @Override
    public void removeStack(T iEffect) {
        if (getEffects().size() == 1) {
            super.removeStack(iEffect);
        }
    }
}
