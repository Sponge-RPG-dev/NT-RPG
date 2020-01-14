package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class SpigotClassGenerator extends ClassGenerator {

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

                EventPriority priority = EventPriority.valueOf(extract(obj, "priority", "NORMAL"));
                i++;

                String methodName = extract(obj, "methodName", "on" + type.getSimpleName() + "" + i);


                AnnotationDescription annotation = AnnotationDescription.Builder.ofType(EventHandler.class)
                        .define("prioerity", priority)
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
