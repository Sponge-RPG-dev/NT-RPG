package cz.neumimto.scripting;

import cz.neumimto.GlobalScope;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.IoC;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.skills.SkillService;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;

import javax.script.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@Singleton
public class JSLoader {
    private static ScriptEngine engine;
    private static File scripts_root = new File(NtRpgPlugin.workingDir + "/scripts");

    @Inject
    private Logger logger;

    @Inject
    private SkillService skillService;

    @Inject
    private IoC ioc;

    @PostProcess(priority = 2)
    public void loadSkills() {
        scripts_root.mkdirs();
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        if (PluginConfig.DEBUG) {
            engine = factory.getScriptEngine(PluginConfig.JJS_ARGS + "d=gen_classes");
        } else {
            engine = factory.getScriptEngine(PluginConfig.JJS_ARGS);
        }
        Path p = Paths.get(scripts_root.toURI());
        try {
            Files.createDirectories(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path path = Paths.get(scripts_root + File.separator + "Main.js");
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("Main.js")) {
                Files.copy(resourceAsStream, path);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try (InputStreamReader rs = new InputStreamReader(new FileInputStream(path.toFile()))) {
            Bindings bindings = new SimpleBindings();
            bindings.put("logger", ioc.logger);
            bindings.put("ioc", ioc);
            bindings.put("GlobalScope", ioc.build(GlobalScope.class));
            engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            engine.eval(rs);
        } catch (ScriptException | IOException e) {
            e.printStackTrace();
        }
    }

}

