package cz.neumimto.rpg.common.skills.scripting;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

public class ScriptSkillBytecodeAppenter implements ByteCodeAppender {

    static Method getDoubleNodeValue;
    static Method getFloatNodeValue;
    static Method getLongNodeValue;
    static Method getIntegerNodeValue;

    static {
        try {
            getDoubleNodeValue = PlayerSkillContext.class.getMethod("getDoubleNodeValue", String.class);
            getFloatNodeValue = PlayerSkillContext.class.getMethod("getFloatNodeValue", String.class);
            getLongNodeValue = PlayerSkillContext.class.getMethod("getLongNodeValue", String.class);
            getIntegerNodeValue = PlayerSkillContext.class.getMethod("getIntNodeValue", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Map<String, RefData> localVariables;

    private TokenizerContext tokenizerctx;
    public ScriptSkillBytecodeAppenter(TokenizerContext tokenizerctx) {
        this.tokenizerctx = tokenizerctx;
        this.localVariables = tokenizerctx.localVariables();
    }

    @Override
    public Size apply(MethodVisitor mv, Implementation.Context ctx, MethodDescription md) {
        boolean lambdaCall = false;
        List<StackManipulation> stackManipulations = new ArrayList<>();
        if (localVariables.containsKey("@caster")) {
            lambdaCall = true;
        } else {

            localVariables.putIfAbsent("@caster", new RefData(MethodVariableAccess.REFERENCE, IActiveCharacter.class, 1));
            localVariables.putIfAbsent("@context", new RefData(MethodVariableAccess.REFERENCE, PlayerSkillContext.class, 2));
            createTargetVariable();


            // for (String mechanic : mechanics) {
            //     if (mechanic.contains("@target")) {
            //         stackManipulations.addAll(createTargetVariable());
            //         break;
            //     }
            // }


            Map<String, MethodVariableAccess> localVars = new HashMap<>();
            for (Parser.Operation operation : tokenizerctx.operations()) {
                localVars.putAll(operation.skillSettingsVarsRequired(tokenizerctx));
            }

            for (Map.Entry<String, MethodVariableAccess> e : localVars.entrySet()) {
                stackManipulations.addAll(skillSettingsIntoLocalVar(e.getKey(), e.getValue()));
            }
        }

        List<Parser.Operation> operations = tokenizerctx.operations();
        for (Parser.Operation operation : operations) {
            stackManipulations.addAll(operation.getStack(tokenizerctx));
        }

        StackManipulation.Size size = new StackManipulation.Compound(
            stackManipulations
        ).apply(mv, ctx);

        return new Size(size.getMaximalSize(), md.getStackSize());
    }

    private int getNextOffset() {
        OptionalInt max = localVariables.values().stream().flatMapToInt(a -> IntStream.of(a.offset)).max();
        return max.getAsInt() + 1;
    }

    private RefData findLocalVariableByName(String name) {
        RefData refData = localVariables.get(name);
        if (refData == null) {
            throw new RuntimeException("Unable to find variable " + name);
        }
        return refData;
    }

    /**
     * generates method
     * double damage = playerSkillContext.getDoubleNodeValue("damage");
     */
    private List<StackManipulation> skillSettingsIntoLocalVar(String mapKey, MethodVariableAccess type) {
        int next = getNextOffset();
        Class<?> ptype = null;
        var stack = Arrays.asList(
                MethodVariableAccess.REFERENCE.loadFrom(localVariables.get("@context").offset),
                new TextConstant(mapKey),
                MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(
                        switch (type) {
                            case LONG -> {
                                ptype = long.class;
                                yield  getLongNodeValue;
                            }
                            case DOUBLE -> {
                                ptype = double.class;
                                yield getDoubleNodeValue;
                            }
                            case FLOAT -> {
                                ptype = float.class;
                                yield getFloatNodeValue;
                            }
                            case INTEGER -> {
                                ptype = int.class;
                                yield getIntegerNodeValue;
                            }
                            default -> throw new IllegalStateException("REFERENCE");
                        }
                )),
                type.storeAt(next)
        );
        localVariables.put(mapKey, new RefData(type, ptype, next));
        return stack;
    }



    /**
     * IEntity target = null
     */
    private List<StackManipulation> createTargetVariable() {
        int nextOffset = getNextOffset();
        localVariables.put("@target", new RefData(MethodVariableAccess.REFERENCE, IEntity.class, nextOffset));
        return Arrays.asList(
                NullConstant.INSTANCE,
                MethodVariableAccess.REFERENCE.storeAt(nextOffset)
        );
    }


    static class RefData {
        MethodVariableAccess type;
        Class<?> aClass;
        int offset;

        public RefData(MethodVariableAccess type, int offset) {
            this.type = type;
            this.offset = offset;
        }

        public RefData(MethodVariableAccess type, Class<?> aClass, int offset) {
            this.type = type;
            this.aClass = aClass;
            this.offset = offset;
        }
    }


   //protected Object getMechanic(String id) {
   //    for (Key<?> key : injector.getAllBindings().keySet()) {
   //        Class<?> rawType = key.getTypeLiteral().getRawType();
   //        if (rawType.isAnnotationPresent(SkillMechanic.class)) {
   //            if (id.equalsIgnoreCase(rawType.getAnnotation(SkillMechanic.class).value())) {
   //                return injector.getInstance(rawType);
   //            }
   //        }
   //    }
   //    return null;
   //}

}
