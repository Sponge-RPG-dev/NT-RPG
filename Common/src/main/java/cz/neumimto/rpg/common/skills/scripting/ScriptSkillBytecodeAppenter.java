package cz.neumimto.rpg.common.skills.scripting;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptSkillBytecodeAppenter {


    public static class CastMethod implements ByteCodeAppender {
        private Map<String, RefData> localVariables;

        private TokenizerContext tokenizerctx;
        public CastMethod(TokenizerContext tokenizerctx) {
            this.tokenizerctx = tokenizerctx;
            this.localVariables = tokenizerctx.localVariables();
        }

        @Override
        public Size apply(MethodVisitor mv, Implementation.Context ctx, MethodDescription md) {
            List<StackManipulation> stackManipulations = new ArrayList<>();

            Map<String, RefData> stringRefDataMap = tokenizerctx.localVariables();;
            for (RefData value : stringRefDataMap.values()) {
                if (value.initInstruction != null) {
                    stackManipulations.addAll(value.initInstruction);
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

            List<Parser.Operation> operations = tokenizerctx.operations();
            for (Parser.Operation operation : operations) {
                stackManipulations.addAll(operation.getStack(tokenizerctx));
            }

            StackManipulation.Size size = new StackManipulation.Compound(
                    stackManipulations
            ).apply(mv, ctx);

            return new Size(size.getMaximalSize(), md.getStackSize());
        }
    }
}
