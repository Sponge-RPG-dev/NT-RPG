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

}
