package cz.neumimto.rpg.api.skills.mods;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;
import cz.neumimto.rpg.skills.mods.SkillPreprocessors;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashSet;

public class SkillPreprocessorFactories {
    public static final SkillPreProcessorFactory UNCASTABLE = new SkillPreProcessorFactory("uncastable", new HashSet<>(Arrays.asList(PreProcessorTarget.EARLY))) {
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            return SkillPreprocessors.NOT_CASTABLE;
        }
    };

    public static final SkillPreProcessorFactory ADJUSTED_SKILL_SETTINGS = new SkillPreProcessorFactory("adjusted_skill_settings_value", new HashSet<>(Arrays.asList(PreProcessorTarget.BEFORE))) {
        final static String expr = "\\{\\{value}}";
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            final String key = configObject.get("Key").unwrapped().toString();
            final String value = configObject.get("Value").unwrapped().toString();

            return new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
                @Override
                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext context) {
                    try {
                        Number eval = (Number) JSLoader.getEngine().eval(value.replaceAll(expr, String.valueOf(context.getSkillNodes().get(key))));
                        context.overrideNode(key, eval.floatValue());
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    context.next(character, info, context);
                }
            };
        }


    };
}
