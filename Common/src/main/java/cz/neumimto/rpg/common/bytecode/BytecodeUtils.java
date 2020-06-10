package cz.neumimto.rpg.common.bytecode;

public class BytecodeUtils {

    public static String classSignature(Class c) {
        return "L" + c.getCanonicalName().replaceAll("\\.","/") + ";";
    }
}
