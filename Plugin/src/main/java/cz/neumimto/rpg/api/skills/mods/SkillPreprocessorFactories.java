package cz.neumimto.rpg.api.skills.mods;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillPreProcessorFactory;
import cz.neumimto.rpg.api.skills.SkillPreprocessors;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.scripting.JSLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.script.ScriptException;
import java.util.*;

public class SkillPreprocessorFactories {
    static final SkillPreProcessorFactory UNCASTABLE = new SkillPreProcessorFactory("uncastable", new HashSet<>(Arrays.asList(PreProcessorTarget.EARLY))) {
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            return SkillPreprocessors.NOT_CASTABLE;
        }
    };

    static final SkillPreProcessorFactory ADJUSTED_SKILL_SETTINGS = new SkillPreProcessorFactory("adjusted_skill_settings_value", new HashSet<>(Arrays.asList(PreProcessorTarget.BEFORE))) {
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

    private static Map<String, SkillPreProcessorFactory> internalCache = new HashMap<>();

    static {
        register(UNCASTABLE);
        register(ADJUSTED_SKILL_SETTINGS);
    }

    public static Optional<SkillPreProcessorFactory> getById(@NonNull String id) {
        return Optional.ofNullable(internalCache.get(id.toLowerCase()));
    }

    public static void register(@NonNull String id, @NonNull SkillPreProcessorFactory l) {
        internalCache.put(id.toLowerCase(), l);
    }

    public static void register(SkillPreProcessorFactory l) {
        register(l.getId(), l);
    }

    public static Collection<SkillPreProcessorFactory> getAll() {
        return Collections.unmodifiableCollection(internalCache.values());
    }
}
