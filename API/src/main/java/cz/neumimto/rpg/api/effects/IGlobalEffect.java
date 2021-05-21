

package cz.neumimto.rpg.api.effects;

import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

import java.util.Map;

/**
 * Created by NeumimTo.
 */
@JsBinding(JsBinding.Type.CLASS)
public interface IGlobalEffect<T extends IEffect> {

    T construct(IEffectConsumer consumer, long duration, Map<String, String> data);

    String getName();

    Class<T> asEffectClass();
}
