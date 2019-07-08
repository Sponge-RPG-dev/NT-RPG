package cz.neumimto.rpg.api.effects.model;

import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.model.mappers.SingleValueModelMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EffectModelFactory {

    static final Map<Class<?>, EffectModelMapper> effectmappers = new HashMap<>();
    static final Map<Class<?>, EffectModelMapper> typeMappers = new HashMap<>();

    static {
        typeMappers.put(Void.TYPE, new SingleValueModelMapper.Void());
        typeMappers.put(Integer.TYPE, new SingleValueModelMapper.Int(Integer.TYPE));
        typeMappers.put(Float.TYPE, new SingleValueModelMapper.Float(Float.TYPE));
        typeMappers.put(Double.TYPE, new SingleValueModelMapper.Double(Double.TYPE));
        typeMappers.put(Long.TYPE, new SingleValueModelMapper.Long(Long.TYPE));
        typeMappers.put(String.class, new SingleValueModelMapper.Str(String.class));
    }

    private EffectModelFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<? extends IEffect> effect, Map<String, String> data, Class<T> expectedType) {
        if (expectedType == Void.class) {
            return null;
        }
        if (effectmappers.containsKey(effect)) {
            return (T) effectmappers.get(effect).parse(data);
        }
        EffectModelMapper effectMapper = getEffectMapper(effect);
        effectmappers.put(effect, effectMapper);
        return (T) effectMapper.parse(data);
    }

    private static EffectModelMapper getEffectMapper(Class<? extends IEffect> iEffect) {
        Class<?> modelType = getModelType(iEffect);
        if (typeMappers.containsKey(modelType)) {
            return typeMappers.get(modelType);
        }
        throw new MissingEffectModelMapper("Could not find a model mapper for a class: " + modelType);
    }


    public static Class<?> getModelType(Class effect) {
        Constructor c = null;
        for (Constructor<?> constructor : effect.getConstructors()) {
            if (constructor.getAnnotation(Generate.Constructor.class) != null ||
                    (constructor.getParameterCount() == 3 &&
                            Arrays.stream(effect.getConstructors())
                                    .map(Constructor::getParameterCount)
                                    .filter(a -> a == 3)
                                    .count() == 1
                    )) {
                c = constructor;
                c.setAccessible(true);
                break;
            }
            //todo use annotation
            if (constructor.getParameterCount() == 2) {
                return Void.TYPE;
            }
        }
        if (c == null) {
            return null;
        }
        Parameter parameter = c.getParameters()[c.getParameters().length - 1];
        return parameter.getType();
    }


    public static Map<Class<?>, EffectModelMapper> getEffectmappers() {
        return effectmappers;
    }

    public static Map<Class<?>, EffectModelMapper> getTypeMappers() {
        return typeMappers;
    }

    public static class MissingEffectModelMapper extends RuntimeException {
        public MissingEffectModelMapper(String message) {
            super(message);
        }
    }
}
