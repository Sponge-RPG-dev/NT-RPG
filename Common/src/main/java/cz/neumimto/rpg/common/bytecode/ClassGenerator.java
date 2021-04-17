package cz.neumimto.rpg.common.bytecode;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;

import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public abstract class ClassGenerator {

    public abstract Type getListenerSubclass();

    public abstract DynamicType.Builder<Object> visitImplSpecAnnListener(ReceiverTypeDefinition<Object> classBuilder, Object object);

}
