package cz.neumimto.rpg.api.effects;

/**
 * Created by NeumimTo on 5.6.17.
 */
public class SingleEffectInstanceContainer<K, T extends IEffect<K>> extends EffectContainer<K, T> {

    public SingleEffectInstanceContainer(T t) {
        super(t);
    }


    @Override
    public void stackEffect(T t, IEffectSourceProvider effectSourceProvider) {
        T a = effects.stream().findFirst().get();
        if (t.getDuration() == -1 && a.getDuration() != -1) {
            a.setDuration(-1);
        }
    }

    @Override
    public void removeStack(T iEffect) {
        if (getEffects().size() == 1) {
            super.removeStack(iEffect);
        }
    }
}
