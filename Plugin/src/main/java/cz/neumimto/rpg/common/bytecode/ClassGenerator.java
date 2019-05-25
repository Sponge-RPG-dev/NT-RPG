package cz.neumimto.rpg.common.bytecode;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.FieldDefinition;
import static net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import static net.bytebuddy.matcher.ElementMatchers.isGetter;
import static net.bytebuddy.matcher.ElementMatchers.isSetter;

public abstract class ClassGenerator {

    public abstract Object generateDynamicListener(List<ScriptObjectMirror> list);

    public <T> Class<T> generatePojoFromInterface(Class<? extends T> iface, ClassLoader loader) {
        Map<String, Class> fields = new HashMap<>();

        Class clazz = iface;
        while (clazz != Object.class) {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                String name = declaredMethod.getName();
                if (name.startsWith("get")) {
                    Class<?> returnType = declaredMethod.getReturnType();
                    String fn = declaredMethod.getName().substring(3, 4).toLowerCase() + declaredMethod.getName().substring(4);
                    fields.put(fn, returnType);
                } else if (name.startsWith("set")) {
                    Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                    String fn = declaredMethod.getName().substring(3, 4).toLowerCase() + declaredMethod.getName().substring(4);
                    fields.put(fn, parameterType);
                } else if (name.startsWith("is")) {
                    Class<?> returnType = declaredMethod.getReturnType();
                    String fn = declaredMethod.getName().substring(1, 2).toLowerCase() + declaredMethod.getName().substring(2);
                    fields.put(fn, returnType);
                } else {
                    throw new IllegalArgumentException("Cannot guess field " + clazz.getName() + "." + name);
                }
            }
            clazz = iface.getSuperclass();
        }

        ReceiverTypeDefinition<T> builder = new ByteBuddy()
                .subclass(clazz)
                .implement(iface)
                .method(isGetter().or(isSetter()))
                .intercept(FieldAccessor.ofBeanProperty());

        FieldDefinition.Optional.Valuable<T> fieldBuilder = null;
        for (Map.Entry<String, Class> stringClassEntry : fields.entrySet()) {
            Type fieldType = stringClassEntry.getValue();
            if (fieldBuilder == null) {
                fieldBuilder = builder.defineField(stringClassEntry.getKey(), fieldType, Visibility.PRIVATE);
            } else {
                fieldBuilder = fieldBuilder.defineField(stringClassEntry.getKey(), fieldType, Visibility.PRIVATE);
            }
        }

        DynamicType.Unloaded<T> make = fieldBuilder == null ? builder.make() : fieldBuilder.make();

        DynamicType.Loaded<T> load = make.load(loader);
        return (Class<T>) load.getLoaded();
    }

}
