package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.effects.IGlobalEffect;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 12.10.15.
 */
@Singleton
public class ClassGenerator implements Opcodes {

	protected static Map<Class<?>, String[]> signaturedictionary = new HashMap<>();

	static {
		signaturedictionary.put(int.class, new String[]{"java/lang/Integer", "intValue", "I"});
		signaturedictionary.put(Integer.class, new String[]{"java/lang/Integer", "intValue", "I"});
		signaturedictionary.put(double.class, new String[]{"java/lang/Double", "doubleValue", "D"});
		signaturedictionary.put(Double.class, new String[]{"java/lang/Double", "doubleValue", "D"});
		signaturedictionary.put(Float.class, new String[]{"java/lang/Float", "floatValue", "F"});
		signaturedictionary.put(float.class, new String[]{"java/lang/Float", "floatValue", "F"});
		signaturedictionary.put(Long.class, new String[]{"java/lang/Long", "longValue", "J"});
		signaturedictionary.put(long.class, new String[]{"java/lang/Long", "longValue", "J"});

		signaturedictionary.put(String.class, new String[]{"java/lang/String", "toString", "Ljava/lang/String;"});
	}

	private String packagee = "cz/neumimto/rpg/asm/";

	public ClassGenerator() {
	}

	private String toPath(Class<?> cl) {
		return cl.getName().replaceAll("\\.", "/");
	}

	public Object generateDynamicListener(List<ScriptObjectMirror> list) {
		Object o = null;
		try {
			String name = "DynamicListener" + System.currentTimeMillis();

			DynamicType.Builder<Object> classBuilder = new ByteBuddy()
					.subclass(Object.class)
					.name(name);

			int i = 0;
			for (ScriptObjectMirror obj : list) {
				Class<?> type = ((StaticClass)obj.get("type")).getRepresentedClass();
				Consumer consumer = (Consumer) obj.get("consumer");
				boolean beforeModifications = extract(obj,"beforeModifications", false);
				Order order = Order.valueOf(extract(obj, "order", "DEFAULT"));
				i++;
				String methodName = extract(obj, "methodName", "on" + type.getSimpleName() + "" + i);


				AnnotationDescription annotation = AnnotationDescription.Builder.ofType(Listener.class)
						.define("beforeModifications", beforeModifications)
						.define("order", order)
						.build();


				classBuilder = classBuilder.defineMethod(methodName, void.class, Visibility.PUBLIC)
							.withParameter(type)
							.intercept(MethodDelegation.to(new EventHandlerInterceptor(consumer)))
							.annotateMethod(annotation);


			}
			Class<?> loaded = classBuilder.make().load(getClass().getClassLoader()).getLoaded();
			o = loaded.newInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static class EventHandlerInterceptor {
		private final Consumer consumer;

		public EventHandlerInterceptor(Consumer consumer) {
			this.consumer = consumer;
		}

		public void intercept(@Argument(0) Object object) {
			this.consumer.accept(object);
		}
	}

	private <T> T extract(ScriptObjectMirror obj, String key, T def) {
		return obj.hasMember(key) ? (T) obj.get(key) : def;
	}

	protected Class<?> loadClass(String className, byte[] b, ClassLoader loader) {
		Class<?> clazz = null;
		try {
			info("Loading class " + className + " size " + b.length + " using a classloader " + loader.toString());
			//ClassLoader loader = getClass().getClassLoader();
			Class<?> cls = Class.forName("java.lang.ClassLoader");
			java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);

			method.setAccessible(true);
			try {
				Object[] args = new Object[]{className, b, 0, b.length};
				clazz = (Class<?>) method.invoke(loader, args);
			} finally {
				method.setAccessible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public <T extends IEffect> void injectGlobalEffectField(Class<T> t, IGlobalEffect<T> toInject) {
		Generate g = t.getAnnotation(Generate.class);
		if (g.inject()) {
			Stream.of(t.getFields())
					.filter(f -> f.getType().isAssignableFrom(IGlobalEffect.class) && Modifier.isStatic(f.getModifiers()))
					.forEach(f -> {
						try {
							f.set(null, toInject);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					});
		}
	}


}
