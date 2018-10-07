package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.utils.SkillModifier;

/**
 * Created by NeumimTo on 7.10.2018.
 */
@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface PassiveScriptSkillHandler {

	void init(IEffectConsumer consumer, ExtendedSkillInfo info, SkillModifier skillModifier, SkillScriptContext context);
}
