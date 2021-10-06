package cz.neumimto.rpg.api.scripting;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.nts.NTScript;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.api.assets.AssetService;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Singleton
public class NTScriptEngine {

    private Map<Class<? extends SkillScriptHandlers>, NTScript> compilers = new HashMap<>();

    @Inject
    private AssetService assetService;

    @Inject
    private Injector injector;

    public boolean canCompile(Class c) {
        return compilers.containsKey(c);
    }

    public NTScript prepareCompiler(List<Object> stl, Class<? extends SkillScriptHandlers> type) {
       return compilers.putIfAbsent(type, scriptContextForSkills(stl, type));
    }

    public NTScript scriptContextForSkills(List<Object> stĺ, Class type) {
        String macros = assetService.getAssetAsString("defaults/nts.macros");

        Map<Pattern, String> macrosMap = new HashMap<>();
        try (BufferedReader bufReader = new BufferedReader(new StringReader(macros))) {
            String line=null;
            while((line=bufReader.readLine()) != null) {
                String[] split = line.split(";;");
                macrosMap.put(Pattern.compile(split[0]),split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return NTScript.builder()
                .implementingType(ScriptSkill.class)
                .classAnnotations(new Class[]{Singleton.class})
                .fieldAnnotation(new Class[]{Inject.class})
                .macro(macrosMap)
                .withEnum(SkillResult.class)
                .implementingType(type)
                .add(stĺ)
                .package_("cz.neumimto.rpg.script.skills")
                .setClassNamePattern("SkillHandler")
                .build();
    }

    public <T extends SkillScriptHandlers> Class<? extends T> compile(String script, Class<T> type) {
        NTScript compiler = prepareCompiler(getStl(), type);
        return (Class<? extends T>) compiler.compile(script);
    }

    public List<Object> getStl() {
        List<Object> list = new ArrayList<>();
        for (Key<?> key : injector.getAllBindings().keySet()) {
            if (key.getTypeLiteral().getRawType().isAnnotationPresent(ScriptMeta.Function.class)) {
                list.add(injector.getInstance(key));
            }
        }
        return list;
    }


}