package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.ScriptEffectBase;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;

/**
 * Multiple classes are generated
 *
 * Assume input
 *   {
 *       Id: Test
 *       Fields: [
 *          Num: numeric
 *       ]
 *       OnApply: """
 *         @effect.Num=50
 *         RETURN
 *       """
 *   }
 * Generates
 *
 * 1) Effect base class
 *
 *  public class Test{timestamp} extends EffectBase {
 *     public double Num;
 *     public static Handler onApply;
 *
 *     public void onApply(IEffect var1) {
 *         if (onApply != null) {
 *             onApply.run(this);
 *         }
 *
 *     }
 *
 *     @ScriptTarget
 *     public Test1637495972021() {
 *         super.effectName = "Test";
 *     }
 * }
 *
 * 2) Handler proxy but with concrete generic type
 *
 * public interface Handler{timestamp} extends Handler<Test{timestamp}> {
 *     @ScriptTarget
 *     void run(@NamedParam("effect") Test{timestamp} var1);
 * }
 *
 * 3) OnApply method as implementation of proxy interface from step 2
 *
 * @Singleton
 * public class HandlerTest{timestamp} implements HandlerTest{timestamp} {
 *     public void run(Test{timestamp} var1) {
 *         var1.Num = 50.0D;
 *     }
 * }
 *
 * This proxy implementation is also automatically initialized with guice injector and its reference is injected into the static field
 * Test{timestamp}.OnApply = injector.newInstance(HandlerTest{timestamp}.class)
 *
 * The timestamps are part of all classnames to ensure easy reloading at runtime, im not reimplementing osgi, just throw away old refs
 *
 */
public class EffectScriptGenerator {

    public static Class<? extends IEffect> from(ScriptEffectModel model, ClassLoader classLoader) {
        try {
            var bb = new ByteBuddy()
                    .subclass(EffectBase.class)
                    .visit(new AsmVisitorWrapper() {
                        @Override
                        public int mergeWriter(int flags) {
                            return flags | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
                        }

                        @Override
                        public int mergeReader(int flags) {
                            return flags | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
                        }

                        @Override
                        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
                            return classVisitor;
                        }
                    })
                    .name("cz.neumimto.rpg.generated.effects." + model.id + System.currentTimeMillis());

            for (Map.Entry<String, String> field : model.fields.entrySet()) {
                String value = field.getValue();
                String key = field.getKey();
                Class type = null;
                if (value.equalsIgnoreCase("numeric")) {
                    type = double.class;
                } else {
                    try {
                        type = Class.forName(value);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                bb = bb.defineField(key, type, Modifier.PUBLIC);
            }

            Field effectName = EffectBase.class.getDeclaredField("effectName");

            Constructor c = null;
            bb = bb.constructor(ElementMatchers.isDefaultConstructor())
                   .intercept(MethodCall.invoke(EffectBase.class.getConstructor())
                           .andThen(new Implementation.Simple(new ByteCodeAppender.Simple(Arrays.asList(
                               MethodVariableAccess.loadThis(),
                               new TextConstant(model.id),
                               FieldAccess.forField(new FieldDescription.ForLoadedField(effectName)).write(),
                               new StackManipulation.Simple((m, i) -> {
                                   m.visitInsn(Opcodes.RETURN);
                                   return new StackManipulation.Size(1,1);
                               }))
                           )))
                   )
                   .annotateMethod(AnnotationDescription.Builder.ofType(ScriptMeta.ScriptTarget.class).build());

            if (model.onApply != null && !model.onApply.isBlank()) {
                bb = generateMethodBody("onApply", bb);
            }
            if (model.onTick != null && !model.onTick.isBlank()) {
                bb = generateMethodBody("onTick", bb);
            }
            if (model.onRemove != null && !model.onRemove.isBlank()) {
                bb = generateMethodBody("onRemove", bb);

            }

            bb.make().saveIn(new File("/tmp"));
            Class<? extends EffectBase> loaded = bb.make()
                    .load(classLoader, INJECTION)
                    .getLoaded();

            injectIfExists("onApply", generateHandler(model.onApply, classLoader, loaded), loaded);
            injectIfExists("onRemove", generateHandler(model.onTick, classLoader, loaded), loaded);
            injectIfExists("onTick", generateHandler(model.onRemove, classLoader, loaded), loaded);
            return loaded;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private static ScriptEffectBase.Handler generateHandler(String script, ClassLoader classLoader, Class<? extends EffectBase> loaded) {
        if (script == null) {
            return null;
        }

        TypeDescription.Generic generic = TypeDescription.Generic.Builder
                .parameterizedType(ScriptEffectBase.Handler.class, loaded).build();

        Class<?> loaded1 = new ByteBuddy()
                .makeInterface(generic)
                .name("cz.neumimto.rpg.generated.effects.Handler" + loaded.getSimpleName() + System.currentTimeMillis())
                .defineMethod("run", void.class, Opcodes.ACC_PUBLIC)
                .withParameter(loaded)
                .annotateParameter(AnnotationDescription.Builder.ofType(ScriptMeta.NamedParam.class).define("value", "effect").build())
                .withoutCode()
                .annotateMethod(AnnotationDescription.Builder.ofType(ScriptMeta.ScriptTarget.class).build())
                .make()
                .load(classLoader, INJECTION)
                .getLoaded();

        Class compile = Rpg.get().getScriptEngine()
                .prepareCompiler(builder -> {}, loaded1)
                .compile(script);
        try {
            Object o = compile.newInstance();
            Rpg.get().getInjector().injectMembers(o);
            return (ScriptEffectBase.Handler) o;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void injectIfExists(String field, ScriptEffectBase.Handler handler, Class<? extends EffectBase> loaded) {
        try {
            loaded.getDeclaredField(field).set(null, handler);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }

    private static DynamicType.Builder<EffectBase> generateMethodBody(String methodName, DynamicType.Builder<EffectBase> bb) {
        bb = bb.defineField(methodName, ScriptEffectBase.Handler.class, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);

        TypeDescription typeDefinitions = bb.toTypeDescription();
        FieldDescription.InDefinedShape field = typeDefinitions.getDeclaredFields().stream()
                .filter(a -> a.getActualName().equalsIgnoreCase(methodName)).findFirst().get();

        Label label1 = new Label();
        Label label2 = new Label();
        Method method = null;
        try {
            method = ScriptEffectBase.Handler.class.getDeclaredMethod("run", EffectBase.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        bb = bb.defineMethod(methodName, void.class, Opcodes.ACC_PUBLIC)
                .withParameters(IEffect.class)
                .intercept(new Implementation.Simple(new ByteCodeAppender.Simple(Arrays.asList(
                        FieldAccess.forField(field).read(),
                        new StackManipulation.Simple((m, i) -> {
                            m.visitJumpInsn(Opcodes.IFNULL, label1);
                            m.visitLabel(label2);
                            return new StackManipulation.Size(1,1);
                        }),
                        FieldAccess.forField(field).read(),
                        MethodVariableAccess.loadThis(),
                        MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(method)),
                        new StackManipulation.Simple((m, i) -> {
                            m.visitLabel(label1);
                            m.visitInsn(Opcodes.RETURN);
                            return new StackManipulation.Size(1,1);
                        })
                ))));
        return bb;
    }

}
