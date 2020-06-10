package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.bytecode.BytecodeUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CustomSkillGenerator {

    private static class InjectImpl implements Inject {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Inject.class;
        }
    }

    public void generate(ScriptSkillModel scriptSkillModel) {
        if (scriptSkillModel.getSpell() == null) {
            return;
        }

        DynamicType.Builder<ActiveSkill> builder = new ByteBuddy()
                .subclass(ActiveSkill.class)
                .name("cz.neumimto.skills.scripts." + System.currentTimeMillis())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", scriptSkillModel.getId())
                        .build())
                .annotateType(AnnotationDescription.Builder.ofType(Singleton.class).build());

        List<Object> mechanics = new ArrayList<>();

        for (Object mechanic : mechanics) {
            builder = builder.defineField(mechanic.getClass().getSimpleName(), mechanic.getClass(), Modifier.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(Inject.class).build());
        }
        builder = builder
                .defineMethod("cast", SkillResult.class, Ownership.MEMBER, Visibility.PUBLIC)
                .withParameters(IActiveCharacter.class, PlayerSkillContext.class)
                .intercept(MethodDelegation.to(new Interceptor(mechanics)));


        Class<? extends ActiveSkill> skillClass = builder.make()
                .load(getClass().getClassLoader())
                .getLoaded();
    }

    public static class Interceptor implements ByteCodeAppender, Opcodes {
        private List<Object> mechanics;

        public Interceptor(List<Object> mechanics) {
            this.mechanics = mechanics;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            ClassWriter classWriter = new ClassWriter(0);

            methodVisitor.visitCode();
            for (Object mechanic : mechanics) {
                String fieldName = mechanic.getClass().getSimpleName();
                String fieldTypeSignature = BytecodeUtils.classSignature(mechanic.getClass());

                //call method
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(GETFIELD, ...);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL,...);

            }
            return null;
        }
    }
}
