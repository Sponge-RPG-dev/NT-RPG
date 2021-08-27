package cz.neumimto.rpg.common.skills.scripting;

import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

import java.util.List;

class RefData {
    MethodVariableAccess type;
    Class<?> aClass;
    int offset;
    List<StackManipulation> initInstruction;

    public RefData(MethodVariableAccess type, Class<?> aClass, int offset) {
        this.type = type;
        this.aClass = aClass;
        this.offset = offset;
    }

    public RefData(MethodVariableAccess type, Class<?> aClass, int offset, List<StackManipulation> initInstruction) {
        this.type = type;
        this.aClass = aClass;
        this.offset = offset;
        this.initInstruction = initInstruction;
    }
}
