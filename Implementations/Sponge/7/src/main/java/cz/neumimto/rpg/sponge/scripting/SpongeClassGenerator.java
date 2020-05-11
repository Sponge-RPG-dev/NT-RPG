package cz.neumimto.rpg.sponge.scripting;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.nashorn.api.scripting.JSObject;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Singleton;
import java.lang.reflect.Type;

/**
 * Created by NeumimTo on 12.10.15.
 */
@Singleton
public class SpongeClassGenerator extends ClassGenerator {

    @Override
    protected Type getListenerSubclass() {
        return Object.class;
    }

    @Override
    protected DynamicType.Builder<Object> visitImplSpecAnnListener(ReceiverTypeDefinition<Object> classBuilder, JSObject obj) {
        boolean beforeModifications = extract(obj, "beforeModifications", false);
        Order order = Order.valueOf(extract(obj, "order", "DEFAULT"));

        AnnotationDescription annotation = AnnotationDescription.Builder.ofType(Listener.class)
                .define("beforeModifications", beforeModifications)
                .define("order", order)
                .build();

        return classBuilder.annotateMethod(annotation);
    }
}
