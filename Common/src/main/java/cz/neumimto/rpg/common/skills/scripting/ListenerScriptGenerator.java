package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.Rpg;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.Opcodes;

import javax.inject.Singleton;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class ListenerScriptGenerator {

    public static Class from(ScriptListenerModel model, ClassLoader classLoader) {
        try {
            var bb = new ByteBuddy()
                    .subclass(Rpg.get().getEventFactory().listenerSubclass())
                    .name("cz.neumimto.rpg.generated.listener." + model.id + System.currentTimeMillis())
                    .annotateType(AnnotationDescription.Builder.ofType(Singleton.class).build())
                    .defineMethod(model.id + "_on" + model.event, void.class, Opcodes.ACC_PUBLIC)
                    .withParameter(Rpg.get().getEventFactory().findBySimpleName(model.event).get().getClass())
                    .annotateParameter(AnnotationDescription.Builder.ofType(ScriptMeta.NamedParam.class).define("value", "event").build())
                    .intercept(new Implementation.Simple(new ByteCodeAppender.Simple(Arrays.asList(
                            MethodReturn.VOID
                    ))))
                    .annotateMethod(AnnotationDescription.Builder.ofType(ScriptMeta.ScriptTarget.class).build())
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            Class compile = Rpg.get().getScriptEngine().prepareCompiler(Rpg.get().getSkillService().getNTSBuilderContext(), bb).compile(model.script);
            return compile;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}
