package cz.neumimto.rpg.skills.mods;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.effects.model.mappers.SingleValueModelMapper;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.utils.TriConsumer;

import javax.script.ScriptException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SkillPreprocessorFactories {
    public static final SkillPreProcessorFactory UNCASTABLE = new SkillPreProcessorFactory("uncastable", PreProcessorTarget.EARLY) {
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            return SkillPreprocessors.NOT_CASTABLE;
        }
    };

    public static final SkillPreProcessorFactory ADJUSTED_SKILL_SETTINGS = new SkillPreProcessorFactory("adjusted_skill_settings_value", PreProcessorTarget.BEFORE) {
        final static String expr = "\\{\\{value}}";
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            final String key = configObject.get("Key").unwrapped().toString();
            final String value = configObject.get("Value").unwrapped().toString();

            return new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
                @Override
                public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
                    try {
                        Number eval = (Number) JSLoader.getEngine().eval(value.replaceAll(expr, String.valueOf(info.getSkillData().getSkillSettings().getNodeValue(key))));
                        info.getSkillData().getSkillSettings().getNodes().put(key, eval.floatValue());
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    skillResult.next(character, info, skillResult);
                }
            };
        }


    };
}
