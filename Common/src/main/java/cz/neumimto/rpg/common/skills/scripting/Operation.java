package cz.neumimto.rpg.common.skills.scripting;

import cz.neumimto.rpg.api.utils.Pair;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Operation {
    List<StackManipulation> getStack(TokenizerContext context);

    default Map<String, MethodVariableAccess> skillSettingsVarsRequired(TokenizerContext context) {
        return Collections.emptyMap();
    }

    default Map<String, List<Operation>> additonalMethods(TokenizerContext context) {
        return Collections.emptyMap();
    }

    default List<Pair<String, String>> variables() {
        return Collections.emptyList();
    }

    default List<FrameVariable> scopeVariables() {
        return new ArrayList<>();
    }

    class FrameVariable {
        String name;
        RefData refData;
    }
}
