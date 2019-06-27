package cz.neumimto.rpg.common.skills.preprocessors;

import com.typesafe.config.ConfigObject;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillPreProcessorFactory;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
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
                        Number eval = (Number) Rpg.get().getScriptEngine().getEngine().eval(value.replaceAll(expr, String.valueOf(context.getSkillNodes().get(key))));
                        context.overrideNode(key, eval.floatValue());
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    context.next(character, info, context);
                }
            };
        }
    };

    static final SkillPreProcessorFactory HAS_EFFECT = new SkillPreProcessorFactory("has_effect", new HashSet<>(Arrays.asList(PreProcessorTarget.BEFORE))) {
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            final String effect = configObject.get("Effect").unwrapped().toString();
            final SkillResult conditionMet = SkillResult.valueOf(configObject.get("ConditionMet").unwrapped().toString().toUpperCase());
            final SkillResult conditionNotMet = SkillResult.valueOf(configObject.get("ConditionNotMet").unwrapped().toString().toUpperCase());

            return new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
                @Override
                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext context) {
                    SkillResult skillResult1 = character.hasEffect(effect) ? conditionMet : conditionNotMet;
                    context.next(character, info, context.result(skillResult1));
                }
            };
        }
    };

    static final SkillPreProcessorFactory HAS_EFFECT_TYPE = new SkillPreProcessorFactory("has_effect_type", new HashSet<>(Arrays.asList(PreProcessorTarget.BEFORE))) {
        @Override
        public ActiveSkillPreProcessorWrapper parse(ConfigObject configObject) {
            final String effect = configObject.get("EffectType").unwrapped().toString();
            final SkillResult conditionMet = SkillResult.valueOf(configObject.get("ConditionMet").unwrapped().toString().toUpperCase());
            final SkillResult conditionNotMet = SkillResult.valueOf(configObject.get("ConditionNotMet").unwrapped().toString().toUpperCase());
            Optional<EffectType> effectType = Rpg.get().getEffectService().getEffectType(effect);
            if (!effectType.isPresent()) {
                Log.error("Unknown effect type " + effect);
                return new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
                    @Override
                    public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                        skillResult.next(character, info, skillResult);
                    }
                };
            }
            EffectType type = effectType.get();
            return new ActiveSkillPreProcessorWrapper(PreProcessorTarget.BEFORE) {
                @Override
                public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext context) {
                    SkillResult skillResult1 = character.hasEffectType(type) ? conditionMet : conditionNotMet;
                    context.next(character, info, context.result(skillResult1));
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
