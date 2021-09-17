package cz.neumimto.rpg.api.scripting;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.nts.NTScript;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class NTScriptEngine {

    private Map<Class<? extends SkillScriptHandlers>, NTScript> compilers = new HashMap<>();

    @Inject
    private Injector injector;

    private NTScript getCompiler(List<Object> stl, Class<? extends SkillScriptHandlers> type) {
       return compilers.putIfAbsent(type, scriptContextForSkills(stl, type));
    }

    public NTScript scriptContextForSkills(List<Object> stĺ, Class type) {
        return NTScript.builder()
                .implementingType(ScriptSkill.class)
                .classAnnotations(new Class[]{Singleton.class})
                .fieldAnnotation(new Class[]{Inject.class})
                .withEnum(SkillResult.class)
                .implementingType(type)
                .add(stĺ)
                .package_("cz.neumimto.rpg.script.skills")
                .setClassNamePattern("SkillHandler")
                .build();
    }

    public <T extends SkillScriptHandlers> Class<? extends T> compile(String script, Class<T> type) {
        NTScript compiler = getCompiler(getStl(), type);
        return (Class<? extends T>) compiler.compile(script);
    }

    private List<Object> getStl() {
        List<Object> list = new ArrayList<>();
        for (Key<?> key : injector.getAllBindings().keySet()) {
            if (key.getTypeLiteral().getRawType().isAnnotationPresent(ScriptMeta.Function.class)) {
                list.add(injector.getInstance(key));
            }
        }
        return list;
    }


}
