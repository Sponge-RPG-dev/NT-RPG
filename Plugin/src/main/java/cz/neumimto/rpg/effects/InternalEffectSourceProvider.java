package cz.neumimto.rpg.effects;

/**
 * Created by ja on 1.4.2017.
 */
public enum InternalEffectSourceProvider implements IEffectSourceProvider {
	INSTANCE {
		@Override
		public IEffectSource getType() {
			return EffectSourceType.INTERNAL;
		}
	};
}
