package cz.neumimto.rpg.common.skills.scripting;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptSkillBytecodeAppenter implements ByteCodeAppender {

    private final List<StackManipulation> stack;

    public ScriptSkillBytecodeAppenter(List<StackManipulation> stack) {
        this.stack = stack;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
        StackManipulation.Size size = new StackManipulation.Compound(
                stack
        ).apply(methodVisitor, implementationContext);

        return new Size(size.getMaximalSize(), instrumentedMethod.getStackSize());
    }


    public static class LambdaMethod implements ByteCodeAppender {
        private Map<String, RefData> localVariables;

        private TokenizerContext tokenizerctx;
        public LambdaMethod(TokenizerContext tokenizerctx) {
            this.tokenizerctx = tokenizerctx;
            this.localVariables = tokenizerctx.localVariables();
        }

        @Override
        public Size apply(MethodVisitor mv, Implementation.Context ctx, MethodDescription md) {
            List<StackManipulation> stackManipulations = new ArrayList<>();

            List<Operation> operations = tokenizerctx.operations();
            for (Operation operation : operations) {
                stackManipulations.addAll(operation.getStack(tokenizerctx));
            }

            StackManipulation.Size size = new StackManipulation.Compound(
                    stackManipulations
            ).apply(mv, ctx);

            return new Size(size.getMaximalSize(), md.getStackSize());
        }
    }
}
