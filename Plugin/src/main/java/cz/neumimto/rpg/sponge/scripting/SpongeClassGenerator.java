package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 12.10.15.
 */
@Singleton
public class SpongeClassGenerator implements Opcodes, ClassGenerator {

    @Override
    public Object generateDynamicListener(List<ScriptObjectMirror> list) {
        Object o = null;
        try {
            String name = "DynamicListener" + System.currentTimeMillis();

            DynamicType.Builder<Object> classBuilder = new ByteBuddy()
                    .subclass(Object.class)
                    .name(name);

            int i = 0;
            for (ScriptObjectMirror obj : list) {
                Class<?> type = ((StaticClass) obj.get("type")).getRepresentedClass();
                Consumer consumer = (Consumer) obj.get("consumer");

                boolean beforeModifications = extract(obj, "beforeModifications", false);
                Order order = Order.valueOf(extract(obj, "order", "DEFAULT"));
                i++;

                String methodName = extract(obj, "methodName", "on" + type.getSimpleName() + "" + i);


                AnnotationDescription annotation = AnnotationDescription.Builder.ofType(Listener.class)
                        .define("beforeModifications", beforeModifications)
                        .define("order", order)
                        .build();


                classBuilder = classBuilder.defineMethod(methodName, void.class, Visibility.PUBLIC)
                        .withParameter(type)
                        .intercept(MethodDelegation.to(new EventHandlerInterceptor(consumer)))
                        .annotateMethod(annotation);


            }
            Class<?> loaded = classBuilder.make().load(getClass().getClassLoader()).getLoaded();
            o = loaded.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static class EventHandlerInterceptor {
        private final Consumer consumer;

        public EventHandlerInterceptor(Consumer consumer) {
            this.consumer = consumer;
        }

        public void intercept(@Argument(0) Object object) {
            this.consumer.accept(object);
        }
    }

    private <T> T extract(ScriptObjectMirror obj, String key, T def) {
        return obj.hasMember(key) ? (T) obj.get(key) : def;
    }
}
