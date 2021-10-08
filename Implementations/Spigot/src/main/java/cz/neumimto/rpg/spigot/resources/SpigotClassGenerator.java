package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;

@Singleton
public class SpigotClassGenerator extends ClassGenerator {

    @Inject
    private IRpgScriptEngine scriptEngine;

    @Override
    public Type getListenerSubclass() {
        return Listener.class;
    }

    @Override
    public DynamicType.Builder<Object> visitImplSpecAnnListener(DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Object> classBuilder, Object obj) {
        EventPriority priority = EventPriority.valueOf(scriptEngine.extract(obj, "priority", "NORMAL"));
        AnnotationDescription annotation = AnnotationDescription.Builder.ofType(EventHandler.class)
                .define("priority", priority)
                .build();

        return classBuilder.annotateMethod(annotation);
    }

}
