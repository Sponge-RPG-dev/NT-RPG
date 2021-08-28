package cz.neumimto.rpg.common.skills.scripting;

import net.bytebuddy.description.type.TypeDescription;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record TokenizerContext(
        Map<String, RefData> localVariables,
        TypeDescription thisType,
        Set<Object> mechanics,
        List<Operation> operations
) {

    public TokenizerContext copyContext(List<Operation> operations) {
        return new TokenizerContext(localVariables, thisType, mechanics, operations);
    }
}
