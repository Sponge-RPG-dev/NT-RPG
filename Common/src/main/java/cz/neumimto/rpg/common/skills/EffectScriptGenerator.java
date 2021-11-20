package cz.neumimto.rpg.common.skills;

import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.ScriptEffectBase;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import static cz.neumimto.rpg.common.effects.ScriptEffectBase.onApply;

public class EffectScriptGenerator {

    public Class<? extends IEffect> from(ScriptEffectModel model, ClassLoader classLoader) {
        Class<?> superType = null;

        var bb = new ByteBuddy()
                .subclass(EffectBase.class)
                .name("cz.neumimto.rpg.generated.effects." + model.id);

        for (Map.Entry<String, String> field : model.fields.entrySet()) {
            String value = field.getValue();
            String key = field.getKey();
            Class type = null;
            if (key.equalsIgnoreCase("numeric")) {
                type = double.class;
            } else {
                try {
                    type = Class.forName(key);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            bb = bb.defineField(value, type, Modifier.PUBLIC);
        }

        Field effectName = null;
        try {
            effectName = EffectBase.class.getDeclaredField("effectName");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        bb = bb.defineConstructor(Modifier.PUBLIC)
                .intercept(new Implementation.Simple(new ByteCodeAppender.Simple(Arrays.asList(
                        MethodVariableAccess.loadThis(),
                        new TextConstant(model.id),
                        FieldAccess.forField(new FieldDescription.ForLoadedField(effectName)).write()
                ))));

        ScriptEffectBase.Handler onApply = null;
        ScriptEffectBase.Handler onTick = null;
        ScriptEffectBase.Handler onRemove = null;

        if (model.onApply != null && !model.onApply.isBlank()) {
            bb = generateMethodBody("onApply", bb);
            onApply = generateHandler(model.onApply);
        }
        if (model.onTick != null && !model.onTick.isBlank()) {
            bb = generateMethodBody("onTick", bb);
        }
        if (model.onRemove != null && !model.onRemove.isBlank()) {
            bb = generateMethodBody("onRemove", bb);
        }
        try {
            bb.make().saveIn(new File("/tmp/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<? extends EffectBase> loaded = bb.make().load(classLoader).getLoaded();
        injectIfExists("onApply", onApply, loaded);
        injectIfExists("onRemove", onRemove, loaded);
        injectIfExists("onTick", onTick, loaded);
        return loaded;
    }

    private ScriptEffectBase.Handler generateHandler(String onApply) {
        Class compile = Rpg.get().getScriptEngine().prepareCompiler(builder -> {
                }, ScriptEffectBase.Handler.class)
                .compile(onApply);
        try {
            Object o = compile.newInstance();
            Rpg.get().getInjector().injectMembers(o);
            return (ScriptEffectBase.Handler) o;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void injectIfExists(String field, ScriptEffectBase.Handler handler, Class<? extends EffectBase> loaded) {
        try {
            loaded.getDeclaredField(field).set(handler, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }

    private DynamicType.Builder<EffectBase> generateMethodBody(String methodName, DynamicType.Builder<EffectBase> bb) {
        bb = bb.defineField(methodName, ScriptEffectBase.Handler.class, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
        TypeDescription typeDefinitions = bb.toTypeDescription();
        FieldDescription.InDefinedShape field = typeDefinitions.getDeclaredFields().stream().filter(a -> a.getActualName().equals(onApply)).findFirst().get();

        Label label1 = new Label();
        Label label2 = new Label();
        Method method = null;
        try {
            method = ScriptEffectBase.Handler.class.getDeclaredMethod("run", EffectBase.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        bb = bb.defineMethod(methodName, void.class, Modifier.PUBLIC)
                .withParameter(IEffect.class)
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
