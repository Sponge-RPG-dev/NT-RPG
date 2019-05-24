package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.common.scripting.JsBinding;

/**
 * Created by NeumimTo on 7.10.2018.
 */
@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface PassiveScriptSkillHandler {

	void init(IEffectConsumer consumer, PlayerSkillContext info, SkillScriptContext context);
}
