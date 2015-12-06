package cz.neumimto;

import cz.neumimto.effects.IEffect;
import cz.neumimto.effects.IGlobalEffect;
import javassist.CannotCompileException;
import org.objectweb.asm.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * Created by NeumimTo on 12.10.15.
 */
@cz.neumimto.core.ioc.Singleton
public class ClassGenerator implements Opcodes {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Generate {
        String id();

        boolean inject() default false;
    }

    public ClassGenerator() {
    }

    private String packagee = "cz/neumimto/asm/effects/";

    private String getCannonicalName(Class cl) {
        return packagee + "Global" + cl.getSimpleName();
    }

    private String toPath(Class cl) {
        return cl.getName().replaceAll("\\.", "/");
    }

    private byte[] generateClass(Class<? extends IEffect> cls,String id) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, getCannonicalName(cls), "Ljava/lang/Object;Lcz/neumimto/effects/IGlobalEffect<L" + toPath(cls) + ";>;", "java/lang/Object", new String[]{"cz/neumimto/effects/IGlobalEffect"});
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
            mv.visitLocalVariable("this", "L" + getCannonicalName(cls) + ";", null, l0, l2, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "construct", "(Lcz/neumimto/effects/IEffectConsumer;JF)L" + toPath(cls) + ";", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(38, l0);
            mv.visitTypeInsn(NEW, toPath(cls));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(LLOAD, 2);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, toPath(cls), "<init>", "(Lcz/neumimto/effects/IEffectConsumer;JF)V", false);
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + getCannonicalName(cls) + ";", null, l0, l1, 0);
            mv.visitLocalVariable("consumer", "Lcz/neumimto/effects/IEffectConsumer;", null, l0, l1, 1);
            mv.visitLocalVariable("duration", "J", null, l0, l1, 2);
            mv.visitLocalVariable("level", "F", null, l0, l1, 4);
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
            mv.visitLocalVariable("this", "L" + getCannonicalName(cls) + ";", null, l0, l1, 0);
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
            mv.visitLocalVariable("this", "L" + getCannonicalName(cls) + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "construct", "(Lcz/neumimto/effects/IEffectConsumer;JF)Lcz/neumimto/effects/IEffect;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(32, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(LLOAD, 2);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, getCannonicalName(cls), "construct", "(Lcz/neumimto/effects/IEffectConsumer;JF)L"+toPath(cls)+";", false);
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + getCannonicalName(cls) + ";", null, l0, l1, 0);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    public IGlobalEffect<? extends IEffect> generateGlobalEffect(Class<? extends IEffect> cls) throws CannotCompileException, IllegalAccessException, InstantiationException {
        Generate annotation = cls.getAnnotation(Generate.class);
        String id = null;
        try {
            id = (String) cls.getDeclaredField(annotation.id()).get(null);
        } catch (NoSuchFieldException e) {
            System.out.println("Could not generate a class from " + cls.getName() +  " make sure id  value of @Generate matches if field name. The field must be public static and type of String");
        }
        byte[] b = generateClass(cls,id);
        Class c = loadClass(getCannonicalName(cls).replaceAll("/", "."), b);
        IGlobalEffect o = (IGlobalEffect) c.newInstance();
        return o;
    }

    private Class loadClass(String className, byte[] b) {
        Class clazz = null;
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Class cls = Class.forName("java.lang.ClassLoader");
            java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);

            method.setAccessible(true);
            try {
                Object[] args = new Object[]{className, b, 0, b.length};
                clazz = (Class) method.invoke(loader, args);
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
