package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.api.effects.EffectSourceType;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

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
