package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.common.skills.scripting.JsBinding;

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
