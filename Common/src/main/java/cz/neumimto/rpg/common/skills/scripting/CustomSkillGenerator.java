package cz.neumimto.rpg.common.skills.scripting;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.pool.TypePool;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CustomSkillGenerator {

    @Inject
    private Injector injector;

    public Class<? extends ISkill> generate(ScriptSkillModel scriptSkillModel, ClassLoader classLoader) throws Exception {
        if (scriptSkillModel == null || scriptSkillModel.getScript() == null || scriptSkillModel.getScript().isEmpty()) {
            return null;
        }

        Parser.ParseTree parse = new Parser().parse(scriptSkillModel.getScript());


        String skillId = scriptSkillModel.getId();

        String className = "Custom" + System.currentTimeMillis();

        Class sk = null;

        DynamicType.Builder<ActiveSkill> bb = new ByteBuddy()
                .subclass(ActiveSkill.class)
                .name("cz.neumimto.skills.scripts." + className)
                .visit(new EnableFramesComputing())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", skillId)
                        .build())
                .annotateType(AnnotationDescription.Builder.ofType(Singleton.class).build());

        for (String requiredMechanic : parse.requiredMechanics()) {
            Object o = filterMechanicById(requiredMechanic);
            bb = bb.defineField(o.getClass().getSimpleName(), o.getClass(), Visibility.PROTECTED)
                    .annotateField(AnnotationDescription.Builder.ofType(Inject.class).build());

        }

        var localVariables = new HashMap<String, ScriptSkillBytecodeAppenter.RefData>();
        var tokenizerctx = new TokenizerContext(localVariables, bb.toTypeDescription(), getMechanics(), parse.operations());

        bb = bb.defineMethod("cast", SkillResult.class, Visibility.PUBLIC)
                .withParameter(characterClassImpl(), "caster")
                .withParameter(PlayerSkillContext.class, "context")
                .intercept(new Implementation() {
                    @Override
                    public InstrumentedType prepare(InstrumentedType instrumentedType) {
                        return instrumentedType;
                    }

                    @Override
                    public ByteCodeAppender appender(Target implementationTarget) {
                        return new ScriptSkillBytecodeAppenter(tokenizerctx);
                    }

                });

        for (Parser.Operation operation : parse.operations()) {
            Map<String, List<Parser.Operation>> map = operation.additonalMethods(tokenizerctx);
            for (Map.Entry<String, List<Parser.Operation>> stringListEntry : map.entrySet()) {
                bb = bb.defineMethod(stringListEntry.getKey(), Void.class, Visibility.PRIVATE, SyntheticState.SYNTHETIC, Ownership.STATIC)
                        .withParameters(tokenizerctx.localVariables().values().stream().map(a->a.aClass).collect(Collectors.toList()))
                        .intercept(new Implementation() {
                            @Override
                            public ByteCodeAppender appender(Target implementationTarget) {
                                return new ScriptSkillBytecodeAppenter(tokenizerctx.copyContext(stringListEntry.getValue()));
                            }

                            @Override
                            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                                return instrumentedType;
                            }
                        });
            }
        }

        DynamicType.Unloaded<ActiveSkill> make = bb.make();
        make.saveIn(new File("/tmp/test.class"));
        return make.load(classLoader).getLoaded();
    }



    protected Set<Object> getMechanics() {
        Set<Object> skillMechanics = new HashSet<>();
        for (Key<?> key : injector.getAllBindings().keySet()) {
            Class<?> rawType = key.getTypeLiteral().getRawType();
            if (hasAnnotation(rawType)) {
                skillMechanics.add(injector.getInstance(rawType));
            }
        }
        return skillMechanics;
    }

    protected Object filterMechanicById(String id) {
        for (Object mechanic : getMechanics()) {
            String annotationId = getAnnotationId(mechanic.getClass());
            if (id != null && id.split(" ")[0].equalsIgnoreCase(annotationId)) {
                return mechanic;
            }
        }
        throw new IllegalStateException("Unknown mechanic id " + id);
    }

    protected boolean hasAnnotation(Class<?> c) {
        return c.isAnnotationPresent(SkillMechanic.class) || c.isAnnotationPresent(TargetSelector.class);
    }

    protected String getAnnotationId(Class<?> rawType) {
        return rawType.isAnnotationPresent(SkillMechanic.class) ?
                rawType.getAnnotation(SkillMechanic.class).value() : rawType.getAnnotation(TargetSelector.class).value();
    }

    protected static Optional<Method> getRelevantMethod(Class<?> rawType) {
        return Stream.of(rawType.getDeclaredMethods())
                .filter(CustomSkillGenerator::isHandlerMethod)
                .findFirst();

    }

    protected static Optional<Method> getRelevantMethod(Object rawType) {
        return getRelevantMethod(rawType.getClass());
    }

    private static boolean isHandlerMethod(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && (method.isAnnotationPresent(Handler.class) || hasAnnotatedArgument(method));
    }

    private static boolean hasAnnotatedArgument(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation a : parameterAnnotation) {
                if (isOneOf(a, Caster.class, Target.class, SkillArgument.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isOneOf(Annotation a, Class<?>... c) {
        for (Class<?> aClass : c) {
            if (is(a, aClass)) {
                return true;
            }
        }
        return false;
    }

    private static boolean is(Annotation a, Class<?> c) {
        return a.annotationType() == c || a.getClass() == c;
    }

    protected abstract Object translateDamageType(String damageType);

    protected abstract String getDefaultEffectPackage();

    protected abstract Type characterClassImpl();

    protected abstract Class<?> targeted();

    static class EnableFramesComputing implements AsmVisitorWrapper {
        @Override
        public final int mergeWriter(int flags) {
            return flags | ClassWriter.COMPUTE_FRAMES;
        }

        @Override
        public final int mergeReader(int flags) {
            return flags | ClassWriter.COMPUTE_FRAMES;
        }

        @Override
        public net.bytebuddy.jar.asm.ClassVisitor wrap(TypeDescription instrumentedType, net.bytebuddy.jar.asm.ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return classVisitor;
        }
    }



}
