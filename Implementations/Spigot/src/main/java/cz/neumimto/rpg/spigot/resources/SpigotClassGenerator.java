package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.nashorn.api.scripting.JSObject;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class SpigotClassGenerator extends ClassGenerator {

    @Override
    protected Type getListenerSubclass() {
        return Listener.class;
    }

    @Override
    protected DynamicType.Builder<Object> visitImplSpecAnnListener(DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Object> classBuilder, JSObject obj) {
        EventPriority priority = EventPriority.valueOf(extract(obj, "priority", "NORMAL"));
        AnnotationDescription annotation = AnnotationDescription.Builder.ofType(EventHandler.class)
                .define("priority", priority)
                .build();

        return classBuilder.annotateMethod(annotation);
    }

}
