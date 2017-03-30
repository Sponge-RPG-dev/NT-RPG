package cz.neumimto.rpg.effects;

/**
 * Created by fs on 30.3.17.
 */
public class EffectSources {
    public static abstract class LEGGINGS implements IEffectSource {
        @Override
        public EffectSourceType getEffectSourceType() {
            return EffectSourceType.LEGGINGS;
        }
    }

    public static abstract class CHESTPLACE implements IEffectSource {
        @Override
        public EffectSourceType getEffectSourceType() {
            return EffectSourceType.CHESTPLATE;
        }
    }

    public static abstract class BOOTS implements IEffectSource {
        @Override
        public EffectSourceType getEffectSourceType() {
            return EffectSourceType.BOOTS;
        }
    }

    public static abstract class HELMET implements IEffectSource {
        @Override
        public EffectSourceType getEffectSourceType() {
            return EffectSourceType.HELMET;
        }
    }
}
