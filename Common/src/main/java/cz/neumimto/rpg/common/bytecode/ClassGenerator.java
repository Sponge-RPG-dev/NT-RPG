package cz.neumimto.rpg.common.bytecode;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class ClassGenerator {

    @Inject
    private ResourceLoader resourceLoader;

    public void generateDynamicListener(List<JSObject> list) {
        String name = "DynamicListener" + System.currentTimeMillis();

        DynamicType.Builder classBuilder = new ByteBuddy()
                .subclass(getListenerSubclass())
                .name(name);

        int i = 0;
        try {
            for (JSObject object : list) {
                if (!object.hasMember("consumer") || !((JSObject)object.getMember("consumer")).isFunction()) {
                    Log.warn("JS event listener missing function consumer, skipping");
                    continue;
                }
                //todo Why binding wont work here?
                //Consumer consumer = jsLoader.toInterface((JSObject) object.getMember("consumer"), Consumer.class);
                //jsLoader.toInterface((JSObject) o, Consumer.class);

                ScriptObjectMirror mirror = (ScriptObjectMirror) object.getMember("consumer");

                Consumer consumer = o -> mirror.call(mirror, o);


                String className = "";
                if (!object.hasMember("type")) {
                    Log.warn("Js event listener missing node type, skipping");
                    continue;
                }
                Object type1 = object.getMember("type");
                if (type1 instanceof StaticClass) {
                    StaticClass staticClass = (StaticClass) type1;
                    className = staticClass.getRepresentedClass().getName();
                    Log.warn("JS event listener for the event " + className + ", it's no longer needed to reference the class (Java.type(...)), use only the wrapped string");
                } else {
                    className = (String) type1;
                }

                Class<?> type = null;
                try {
                    type = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    Log.warn("JS event listener - unknown event " + className);
                    continue;
                }
                i++;
                String methodName = extract(object, "methodName", "on" + type.getSimpleName() + "" + i);

                ReceiverTypeDefinition intercept = classBuilder
                        .defineMethod(methodName, void.class, Visibility.PUBLIC)
                        .withParameter(type)
                        .intercept(MethodDelegation.to(new EventHandlerInterceptor(consumer)));

                classBuilder = visitImplSpecAnnListener(intercept, object);
            }
            Class<?> loaded = classBuilder.make().load(getClass().getClassLoader()).getLoaded();
            Rpg.get().registerListeners(loaded.newInstance());
        } catch (Throwable t) {
            Log.error("Could not create a dynamic js listener", t);
        }
    }

    protected abstract Type getListenerSubclass();

    protected abstract DynamicType.Builder<Object> visitImplSpecAnnListener(ReceiverTypeDefinition<Object> classBuilder, JSObject object);

    protected <T> T extract(JSObject obj, String key, T def) {
        return obj.hasMember(key) ? (T) obj.getMember(key) : def;
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
}
