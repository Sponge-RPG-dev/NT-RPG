package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;

/**
 * Created by NeumimTo on 7.10.2018.
 */
@FunctionalInterface
@JsBinding(JsBinding.Type.CLASS)
public interface PassiveScriptSkillHandler {

    void init(IEffectConsumer consumer, PlayerSkillContext context);
}
