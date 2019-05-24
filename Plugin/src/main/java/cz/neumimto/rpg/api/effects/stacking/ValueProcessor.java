package cz.neumimto.rpg.api.effects.stacking;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.EffectContainer;

/**
 * Created by NeumimTo on 7.7.2017.
 */
public abstract class ValueProcessor<T extends Number, I extends IEffect<T>> extends EffectContainer<T, I> {


    public ValueProcessor(I numberIEffect) {
        super(numberIEffect);
    }

    @Override
    public void updateStackedValue() {
        setStackedValue(findValue());
    }

    public abstract T findValue();

    public static class D_MAX extends ValueProcessor<Double, IEffect<Double>> {

        public D_MAX(IEffect<Double> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Double findValue() {
            return getEffects().stream().mapToDouble(IEffect::getValue).max().orElse(0D);
        }

    }

    public static class L_MAX extends ValueProcessor<Long, IEffect<Long>> {

        public L_MAX(IEffect<Long> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Long findValue() {
            return getEffects().stream().mapToLong(IEffect::getValue).max().orElse(0L);
        }
    }

    public static class D_MIN extends ValueProcessor<Double, IEffect<Double>> {

        public D_MIN(IEffect<Double> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Double findValue() {
            return getEffects().stream().mapToDouble(IEffect::getValue).min().orElse(0D);
        }

    }

    public static class L_MIN extends ValueProcessor<Long, IEffect<Long>> {

        public L_MIN(IEffect<Long> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Long findValue() {
            return getEffects().stream().mapToLong(IEffect::getValue).min().orElse(0L);
        }

    }


    public static class D_AVG extends ValueProcessor<Double, IEffect<Double>> {

        public D_AVG(IEffect<Double> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Double findValue() {
            return getEffects().stream().mapToDouble(IEffect::getValue).average().orElse(0D);
        }

    }

    public static class L_AVG extends ValueProcessor<Long, IEffect<Long>> {

        public L_AVG(IEffect<Long> floatIEffect) {
            super(floatIEffect);
        }

        @Override
        public Long findValue() {
            return (long) getEffects().stream().mapToLong(IEffect::getValue).average().orElse(0D);
        }

    }
}
