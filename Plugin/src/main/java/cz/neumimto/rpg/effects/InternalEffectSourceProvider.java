package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by ja on 1.4.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public enum InternalEffectSourceProvider implements IEffectSourceProvider {
	INSTANCE {
		@Override
		public IEffectSource getType() {
			return EffectSourceType.INTERNAL;
		}
	}
}
