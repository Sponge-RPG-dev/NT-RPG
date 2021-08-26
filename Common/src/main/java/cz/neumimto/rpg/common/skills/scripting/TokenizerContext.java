package cz.neumimto.rpg.common.skills.scripting;

import net.bytebuddy.description.type.TypeDescription;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record TokenizerContext(
        Map<String, ScriptSkillBytecodeAppenter.RefData> localVariables,
        TypeDescription thisType,
        Set<Object> mechanics,
        List<Parser.Operation> operations
) {

    public TokenizerContext copyContext(List<Parser.Operation> operations) {
        return new TokenizerContext(localVariables, thisType, mechanics, operations);
    }
}
