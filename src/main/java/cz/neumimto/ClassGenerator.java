package cz.neumimto;

import cz.neumimto.effects.IEffect;
import cz.neumimto.effects.IGlobalEffect;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * Created by fs on 12.10.15.
 */
@cz.neumimto.core.ioc.Singleton
public class ClassGenerator {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Generate {
        String id();

        boolean inject() default false;
    }

    public ClassGenerator() {
    }


    public IGlobalEffect<? extends IEffect> generateGlobalEffect(Class<? extends IEffect> cls) throws CannotCompileException, IllegalAccessException, InstantiationException {
        Generate a = cls.getAnnotation(Generate.class);
        ClassPool classPool = ClassPool.getDefault();
        CtClass ct = classPool.makeClass("cz.neumimto.genclasses.Global" + cls.getSimpleName());
        CtClass interfacee = classPool.makeInterface("cz.neumimto.effects.IGlobalEffect");
        ct.addInterface(interfacee);
        String cn = cls.getCanonicalName();
        ct.addMethod(CtMethod.make("public " + cn + " construct(cz.neumimto.effects.IEffectConsumer c, long d, int l) { return new " + cn + "(c,d,l);}", ct));
        ct.addMethod(CtMethod.make("public String getName() { return " + cn + "." + a.id() + ";}", ct));
        ct.addMethod(CtMethod.make("public Class asEffectClass() {return " + cn + ".class;  }", ct));
        Class cl = ct.toClass();
        IGlobalEffect eff = (IGlobalEffect) cl.newInstance();
        return eff;
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
