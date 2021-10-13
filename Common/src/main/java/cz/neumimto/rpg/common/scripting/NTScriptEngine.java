package cz.neumimto.rpg.common.scripting;

import com.google.inject.Injector;
import com.google.inject.Key;
import cz.neumimto.nts.NTScript;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
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
       if (!compilers.containsKey(type)) {
           compilers.put(type, scriptContextForSkills(stl, type));
       }
       return compilers.get(type);
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
                .debugOutput("/tmp")
                .logging(Log::warn)
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

        //instances
        ServiceLoader.load(NTScriptProxy.class, getClass().getClassLoader()).stream()
                .map(ServiceLoader.Provider::get)
                .forEach(a-> list.add(injector.getInstance(a.getClass())));

        //types
        try {
            ServiceLoader.load(IEffect.class, getClass().getClassLoader()).stream()
                    .map(ServiceLoader.Provider::type) //java bug | java 16.0.2
                    .forEach(list::add);

        } catch (Throwable t) {
            Log.info("Java service provider bug still present, doing it the stupid way");

            try {
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.BleedingEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.FeatherFall"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.Maim"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.ManaShieldEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.NoAutohealEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.PiggifyEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.SlowEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.StunEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.UnhealEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.UnlimtedFoodLevelEffect"));
                list.add(Class.forName("cz.neumimto.rpg.spigot.effects.common.WebEffect"));
            } catch (Exception e) {

            }
        }


        return list;
    }


}
