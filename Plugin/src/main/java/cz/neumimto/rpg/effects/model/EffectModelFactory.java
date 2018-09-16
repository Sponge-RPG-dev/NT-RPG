package cz.neumimto.rpg.effects.model;

import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.model.mappers.SingleValueModelMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EffectModelFactory {

	public static Map<Class<?>, EffectModelMapper> effectmappers = new HashMap<>();
	public static Map<Class<?>, EffectModelMapper> typeMappers = new HashMap<>();

	static {
		typeMappers.put(int.class, new SingleValueModelMapper.Int(Integer.class));
		typeMappers.put(Integer.class, new SingleValueModelMapper.Int(Integer.class));

		typeMappers.put(float.class, new SingleValueModelMapper.Float(Float.class));
		typeMappers.put(Float.class, new SingleValueModelMapper.Float(Float.class));

		typeMappers.put(double.class, new SingleValueModelMapper.Double(Double.class));
		typeMappers.put(Double.class, new SingleValueModelMapper.Double(Double.class));

		typeMappers.put(long.class, new SingleValueModelMapper.Long(Long.class));
		typeMappers.put(Long.class, new SingleValueModelMapper.Long(Long.class));

		typeMappers.put(String.class, new SingleValueModelMapper.Str(String.class));
		typeMappers.put(Void.class, new SingleValueModelMapper.Void());
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
		throw new RuntimeException("Could not find a model mapper for a class: " + modelType);

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
				return Void.class;
			}
		}
		if (c == null) {
			throw new RuntimeException("No valid constructor for " + effect.getSimpleName());
		}
		Parameter parameter = c.getParameters()[c.getParameters().length - 1];
		return parameter.getType();
	}


}
