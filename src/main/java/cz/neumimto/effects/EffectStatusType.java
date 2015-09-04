package cz.neumimto.effects;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public enum EffectStatusType {
    APPLIED {
        @Override
        public String toMessage(IEffect effect) {
            return effect.getApplyMessage();
        }
    }, EXPIRED {
        @Override
        public String toMessage(IEffect effect) {
            return effect.getExpireMessage();
        }
    };

    public abstract String toMessage(IEffect effect);
}
