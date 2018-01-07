package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IGlobalEffect;
import javassist.CannotCompileException;
import jdk.internal.dynalink.beans.StaticClass;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.spongepowered.api.event.Event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 12.10.15.
 */
@cz.neumimto.core.ioc.Singleton
public class ClassGenerator implements Opcodes {
	protected static Map<Class<?>, String[]> signaturedictionary = new HashMap<>();

	static {
		signaturedictionary.put(int.class,      new String[]{"java/lang/Integer","intValue", "I"});
		signaturedictionary.put(Integer.class,  new String[]{"java/lang/Integer","intValue", "I"});
		signaturedictionary.put(double.class,   new String[]{"java/lang/Double","doubleValue", "D"});
		signaturedictionary.put(Double.class,   new String[]{"java/lang/Double","doubleValue", "D"});
		signaturedictionary.put(Float.class,    new String[]{"java/lang/Float","floatValue", "F"});
		signaturedictionary.put(float.class,    new String[]{"java/lang/Float","floatValue", "F"});
		signaturedictionary.put(Long.class,     new String[]{"java/lang/Long","longValue", "J"});
		signaturedictionary.put(long.class,     new String[]{"java/lang/Long","longValue", "J"});

		signaturedictionary.put(String.class,     new String[]{"java/lang/String","toString", "Ljava/lang/String;"});
	}

	private String packagee = "cz/neumimto/rpg/asm/";

	public ClassGenerator() {
	}

	private String getCannonicalGlobalName(Class<?> cl) {
		return packagee + "Global" + cl.getSimpleName();
	}

	private String toPath(Class<?> cl) {
		return cl.getName().replaceAll("\\.", "/");
	}

	private byte[] generateEffectClass(Class<? extends IEffect<?>> cls, String id) {
		ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cw = new CheckClassAdapter(cv);
		MethodVisitor mv;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, getCannonicalGlobalName(cls), "Ljava/lang/Object;Lcz/neumimto/rpg/effects/IGlobalEffect<L" + toPath(cls) + ";>;", "java/lang/Object", new String[]{"cz/neumimto/rpg/effects/IGlobalEffect"});
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(33, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(34, l1);
			mv.visitInsn(RETURN);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLocalVariable("this", "L" + getCannonicalGlobalName(cls) + ";", null, l0, l2, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{

			mv = cw.visitMethod(ACC_PUBLIC, "construct", "(Lcz/neumimto/rpg/effects/IEffectConsumer;JLjava/lang/String;)L" + toPath(cls) + ";", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(38, l0);
			mv.visitTypeInsn(NEW, toPath(cls));
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKESPECIAL, toPath(cls), "<init>", "(Lcz/neumimto/rpg/effects/IEffectConsumer;JLjava/lang/String;)V", false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + getCannonicalGlobalName(cls) + ";", null, l0, l1, 0);
			mv.visitLocalVariable("consumer", "Lcz/neumimto/rpg/effects/IEffectConsumer;", null, l0, l1, 1);
			mv.visitLocalVariable("duration", "J", null, l0, l1, 2);
			mv.visitLocalVariable("value", "Ljava/lang/String;", null, l0, l1, 4);
			mv.visitMaxs(6, 5);
			mv.visitEnd();
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(43, l0);
			mv.visitLdcInsn(id);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + getCannonicalGlobalName(cls) + ";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "asEffectClass", "()Ljava/lang/Class;", "()Ljava/lang/Class<L" + toPath(cls) + ";>;", null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(48, l0);
			mv.visitLdcInsn(Type.getType("L" + toPath(cls) + ";"));
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + getCannonicalGlobalName(cls) + ";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "construct", "(Lcz/neumimto/rpg/effects/IEffectConsumer;JLjava/lang/String;)Lcz/neumimto/rpg/effects/IEffect;", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(32, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(LLOAD, 2);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKEVIRTUAL, getCannonicalGlobalName(cls), "construct", "(Lcz/neumimto/rpg/effects/IEffectConsumer;JLjava/lang/String;)L" + toPath(cls) + ";", false);
			mv.visitInsn(ARETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L" + getCannonicalGlobalName(cls) + ";", null, l0, l1, 0);
			mv.visitMaxs(5, 5);
			mv.visitEnd();
		}

		cw.visitEnd();

		return cv.toByteArray();


	}

	public Class<?> createEffectModelMapper(Class<? extends IEffect> cl, Class<?> model) {
		byte[] bytes = generateEffectMapperClass(cl, model);
		String className = cl.getSimpleName()+"Mapper";
		String classNameWithPackage = (packagee+className).replaceAll("/",".");;
		return loadClass(classNameWithPackage, bytes);
	}

	@SuppressWarnings("unchecked")
	private byte[] generateEffectMapperClass(Class<? extends IEffect> cl, Class<?> model) {
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			FieldVisitor fv;
			MethodVisitor mv;
			AnnotationVisitor av0;

			String className = cl.getSimpleName()+"Mapper";
			String classNameWithPackage = (packagee+className).replaceAll("\\.", "/");
			String modelName = model.getName().replace(".","/");
			cw.visit(52, ACC_PUBLIC + ACC_SUPER, classNameWithPackage, null, "cz/neumimto/rpg/effects/model/EffectModelMapper", null);


			cw.visitSource(className+".java", null);
			
			{
				//ctr
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Class;)V", "(Ljava/lang/Class<*>;)V", null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(11, l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				//super
				mv.visitMethodInsn(INVOKESPECIAL, "cz/neumimto/rpg/effects/model/EffectModelMapper", "<init>", "(Ljava/lang/Class;)V", false);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLineNumber(12, l1);
				mv.visitInsn(RETURN);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLocalVariable("this", "L"+classNameWithPackage+";", null, l0, l2, 0);
				mv.visitLocalVariable("t", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l0, l2, 1);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			int line = 17;
			{
				mv = cw.visitMethod(ACC_PUBLIC, "parse", "(Ljava/util/Map;)Ljava/lang/Object;", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)Ljava/lang/Object;", null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(++line, l0);
				//new <model>
				mv.visitTypeInsn(NEW, modelName);
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, modelName, "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 2);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLineNumber(++line, l1);
				mv.visitVarInsn(ALOAD, 2);

				//mv.visitFieldInsn(GETSTATIC, modelName, "typeMapperMap", "Ljava/util/Map;");


				for (Field field : model.getFields()) {
					String fieldName = field.getName();
					Class<?> type = field.getType();
					String[] strings = signaturedictionary.get(type);
					line++;
					if (strings == null)
						throw new RuntimeException(String.format("Cannot create mapper for %s due to %s type", modelName, type));
					if (type.isPrimitive()) {
						this.effectModelFactory_putFieldPrimitive(	mv,
																	line,
																	classNameWithPackage,
																	modelName,
																	strings,
																	fieldName);
					} else {
						this.effectModelFactory_putFieldObjectType(	mv,
																	line,
																	classNameWithPackage,
																	modelName,
																	type.getName().replaceAll("\\.", "/"),
																	fieldName);
					}
					line++;
				}

				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLineNumber(++line, l2);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitInsn(ARETURN);
				Label l3 = new Label();
				mv.visitLabel(l3);

				mv.visitLocalVariable("this", "L"+classNameWithPackage+";", null, l0, l3, 0);
				mv.visitLocalVariable("data", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l3, 1);
				mv.visitLocalVariable("model", "L"+modelName+";", null, l1, l3, 2);

			//	mv.visitLocalVariable("this", "Lcz/neumimto/rpg/effects/model/TestModelMapper;", null, l0, l3, 0);
			//	mv.visitLocalVariable("data", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l3, 1);
			//	mv.visitLocalVariable("model", "L"+modelName+";", null, l1, l3, 2);

				mv.visitMaxs(4, 3);
				mv.visitEnd();
			}
			cw.visitEnd();

			return cw.toByteArray();

	}

	private void effectModelFactory_putFieldObjectType(MethodVisitor mv, int lineNumber, String classNameWithPackage, String modelClassNameWithPackage, String fieldTzpe, String fieldname) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(lineNumber, label);
		mv.visitVarInsn(ALOAD, 2);
		//mv.visitMethodInsn(INVOKEVIRTUAL, classNameWithPackage, "getCache", "()Ljava/util/Map;", false);

		mv.visitFieldInsn(GETSTATIC, "cz/neumimto/rpg/effects/model/EffectModelMapper", "typeMapperMap", "Ljava/util/Map;");
        mv.visitLdcInsn(Type.getType("L"+fieldTzpe+";"));
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(fieldname);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitTypeInsn(CHECKCAST, fieldTzpe);
		mv.visitFieldInsn(PUTFIELD, modelClassNameWithPackage, fieldname, "L"+fieldTzpe+";");
	}

	private void effectModelFactory_putFieldPrimitive(MethodVisitor mv, int lineNumber, String classNameWithPackage, String modelClassNameWithPackage, String[] typeSignature, String fieldName) {
		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(lineNumber, label);
		mv.visitVarInsn(ALOAD, 2);

		//mv.visitMethodInsn(INVOKEVIRTUAL, classNameWithPackage, "getCache", "()Ljava/util/Map;", false);
		mv.visitFieldInsn(GETSTATIC, "cz/neumimto/rpg/effects/model/EffectModelMapper", "typeMapperMap", "Ljava/util/Map;");

        mv.visitFieldInsn(GETSTATIC, typeSignature[0], "TYPE", "Ljava/lang/Class;");
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(fieldName);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		mv.visitTypeInsn(CHECKCAST, typeSignature[0]);
		mv.visitMethodInsn(INVOKEVIRTUAL, typeSignature[0], typeSignature[1], "()"+typeSignature[2], false);
		mv.visitFieldInsn(PUTFIELD, modelClassNameWithPackage, fieldName, typeSignature[2]);
	}

	public IGlobalEffect<? extends IEffect> generateGlobalEffect(Class<? extends IEffect<?>> cls) throws CannotCompileException, IllegalAccessException, InstantiationException {
		Generate annotation = cls.getAnnotation(Generate.class);
		String id = null;
		try {
			id = (String) cls.getDeclaredField(annotation.id()).get(null);
		} catch (NoSuchFieldException e) {
			System.out.println("Could not generate a class from " + cls.getName() + " make sure id  value of @Generate matches if field name. The field must be public static and type of String");
		}

		byte[] b = generateEffectClass(cls, id);
		Class<?> c = loadClass(getCannonicalGlobalName(cls).replaceAll("/", "."), b);
		IGlobalEffect o = (IGlobalEffect) c.newInstance();
		return o;
	}

	public Object generateDynamicListener(Map<StaticClass, Set<Consumer<? extends Event>>> map) {
		Object o = null;
		try {
			byte[] b = generateDynamicListenerbc(map);
			o = loadClass("cz.neumimto.rpg.listeners.DynamicListener", b);
			Class<?> listener = Class.forName("cz.neumimto.rpg.listeners.DynamicListener");
			o = listener.newInstance();
			for (Field field : listener.getDeclaredFields()) {
				if (Set.class.isAssignableFrom(field.getType())) {
					Set s = (Set) field.get(o);
					ParameterizedType paramtype = (ParameterizedType) field.getGenericType();
					ParameterizedType type = (ParameterizedType) paramtype.getActualTypeArguments()[0];
					Class<? extends Event> event = (Class<? extends Event>) type.getActualTypeArguments()[0];
					map.entrySet().stream()
							.filter(m -> m.getKey().getRepresentedClass() == event)
							.forEach(a -> s.addAll(a.getValue()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	protected Class<?> loadClass(String className, byte[] b) {
		Class<?> clazz = null;
		try {
			ClassLoader loader = getClass().getClassLoader();
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

	private byte[] generateDynamicListenerbc(Map<StaticClass, Set<Consumer<? extends Event>>> set) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, "cz/neumimto/rpg/listeners/DynamicListener", null, "java/lang/Object", null);

		cw.visitSource("DynamicListener.java", null);

		{
			av0 = cw.visitAnnotation("Lcz/neumimto/rpg/ResourceLoader$ListenerClass;", true);
			av0.visitEnd();
		}
		cw.visitInnerClass("cz/neumimto/ResourceLoader$ListenerClass", "cz/neumimto/rpg/ResourceLoader", "ListenerClass", ACC_PUBLIC + ACC_STATIC + ACC_ANNOTATION + ACC_ABSTRACT + ACC_INTERFACE);

		for (StaticClass e : set.keySet()) {
			String name = e.getRepresentedClass().getSimpleName().substring(0, 1).toLowerCase() + e.getRepresentedClass().getSimpleName().substring(1) + "s";
			fv = cw.visitField(ACC_PUBLIC, name, "Ljava/util/Set;", "Ljava/util/Set<Ljava/util/function/Consumer<L" + toPath(e.getRepresentedClass()) + ";>;>;", null);
			fv.visitEnd();
		}
		int i = 19;
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(17, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			for (StaticClass a : set.keySet()) {
				Class<?> e = a.getRepresentedClass();
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLineNumber(i, l1);
				i++;
				mv.visitVarInsn(ALOAD, 0);
				mv.visitTypeInsn(NEW, "java/util/HashSet");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false);
				String name = e.getSimpleName().substring(0, 1).toLowerCase() + e.getSimpleName().substring(1) + "s";
				mv.visitFieldInsn(PUTFIELD, "cz/neumimto/rpg/listeners/DynamicListener", name, "Ljava/util/Set;");
			}
			mv.visitInsn(RETURN);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitLocalVariable("this", "Lcz/neumimto/rpg/listeners/DynamicListener;", null, l0, l3, 0);
			mv.visitMaxs(3, 1);
			mv.visitEnd();
		}
		{
			for (StaticClass a : set.keySet()) {
				Class<?> e = a.getRepresentedClass();
				String name = "on" + e.getSimpleName();
				String name1 = e.getSimpleName().substring(0, 1).toLowerCase() + e.getSimpleName().substring(1) + "s";
				mv = cw.visitMethod(ACC_PUBLIC, name, "(L" + toPath(e) + ";)V", null, null);
				{
					av0 = mv.visitAnnotation("Lorg/spongepowered/api/event/Listener;", true);
					av0.visitEnum("order", "Lorg/spongepowered/api/event/Order;", "BEFORE_POST");
					av0.visitEnd();
					mv.visitCode();
					Label l0 = new Label();
					mv.visitLabel(l0);
					i += 3;
					mv.visitLineNumber(i, l0);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, "cz/neumimto/rpg/listeners/DynamicListener", name1, "Ljava/util/Set;");
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
					mv.visitVarInsn(ASTORE, 2);
					Label l1 = new Label();
					mv.visitLabel(l1);
					mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
					Label l2 = new Label();
					mv.visitJumpInsn(IFEQ, l2);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
					mv.visitTypeInsn(CHECKCAST, "java/util/function/Consumer");
					mv.visitVarInsn(ASTORE, 3);
					Label l3 = new Label();
					mv.visitLabel(l3);
					i++;
					mv.visitLineNumber(i, l3);
					mv.visitVarInsn(ALOAD, 3);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
					Label l4 = new Label();
					mv.visitLabel(l4);
					i++;
					mv.visitLineNumber(i, l4);
					mv.visitJumpInsn(GOTO, l1);
					mv.visitLabel(l2);
					i++;
					mv.visitLineNumber(i, l2);
					mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
					mv.visitInsn(RETURN);
					Label l5 = new Label();
					mv.visitLabel(l5);
					mv.visitLocalVariable("it", "Ljava/util/function/Consumer;", "Ljava/util/function/Consumer<L" + toPath(e) + ";>;", l3, l4, 3);
					mv.visitLocalVariable("this", "Lcz/neumimto/rpg/listeners/DynamicListener;", null, l0, l5, 0);
					mv.visitLocalVariable("event", "L" + toPath(e) + ";", null, l0, l5, 1);
					mv.visitMaxs(2, 4);
					mv.visitEnd();
				}
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Generate {
		String id();

		boolean inject() default false;

		@Retention(RetentionPolicy.RUNTIME)
		@interface Constructor {

		}
	}
}
