package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.scripting.AbstractRpgScriptEngine;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;

/**
 * Created by NeumimTo on 12.10.15.
 */
@Singleton
public class SpongeClassGenerator extends ClassGenerator {

    @Inject
    private AbstractRpgScriptEngine scriptEngine;

    @Override
    public Type getListenerSubclass() {
        return Object.class;
    }

    @Override
    public DynamicType.Builder<Object> visitImplSpecAnnListener(ReceiverTypeDefinition<Object> classBuilder, Object obj) {
        boolean beforeModifications = scriptEngine.extract(obj, "beforeModifications", false);
        Order order = Order.valueOf(scriptEngine.extract(obj, "order", "DEFAULT"));

        AnnotationDescription annotation = AnnotationDescription.Builder.ofType(Listener.class)
                .define("beforeModifications", beforeModifications)
                .define("order", order)
                .build();

        return classBuilder.annotateMethod(annotation);
    }
}
