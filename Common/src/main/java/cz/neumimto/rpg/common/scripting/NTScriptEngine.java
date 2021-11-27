package cz.neumimto.rpg.common.scripting;

import com.google.inject.Injector;
import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.scripting.mechanics.NTScriptProxy;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Singleton
public class NTScriptEngine {

    private Map<Class, NTScript> compilers = new HashMap<>();

    @Inject
    private AssetService assetService;

    @Inject
    private Injector injector;

    public List<Class> STL = new ArrayList<>();

    public NTScript prepareCompiler(Consumer<NTScript.Builder> builder, Class type) {
        if (!compilers.containsKey(type)) {
            compilers.put(type, scriptContextForSkills(builder, type));
        }
        return compilers.get(type);
    }

    public NTScript scriptContextForSkills(Consumer<NTScript.Builder> builder, Class type) {
        String macros = assetService.getAssetAsString("defaults/nts.macros");

        Map<Pattern, String> macrosMap = new HashMap<>();
        try (BufferedReader bufReader = new BufferedReader(new StringReader(macros))) {
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                String[] split = line.split(";;");
                macrosMap.put(Pattern.compile(split[0]), split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NTScript.Builder n = NTScript.builder()
                .implementingType(type)
                .classAnnotations(new Class[]{Singleton.class})
                .fieldAnnotation(new Class[]{Inject.class})
                .macro(macrosMap)
                .withEnum(SkillResult.class)
                .debugOutput(Rpg.get().getWorkingDirectory() + File.separator + "/compiled-scripts")
                .add(getStl())
                .logging(Log::warn)
                .package_("cz.neumimto.rpg.script.skills")
                .setClassNamePattern(type.getSimpleName());

        n.add(STL);
        builder.accept(n);

        return n.build();
    }

    public List<Object> getStl() {
        List<Object> list = new ArrayList<>();

        //instances
        ServiceLoader.load(NTScriptProxy.class, getClass().getClassLoader()).stream()
                .map(ServiceLoader.Provider::get)
                .forEach(a -> list.add(injector.getInstance(a.getClass())));

        //types
        try {
            ServiceLoader.load(IEffect.class, getClass().getClassLoader()).stream()
                    .map(ServiceLoader.Provider::type) //java bug | java 16.0.2
                    .forEach(list::add);

        } catch (Throwable t) {
        }

        list.addAll(STL);
        return list;
    }


}
