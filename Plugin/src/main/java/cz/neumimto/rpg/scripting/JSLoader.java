/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.scripting;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.utils.FileUtils;
import jdk.internal.dynalink.beans.StaticClass;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Event;

import javax.script.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@Singleton
public class JSLoader {
    public static ScriptEngine engine;
    private static Path scripts_root = Paths.get(NtRpgPlugin.workingDir + "/scripts");

    @Inject
    private Logger logger;

    @Inject
    private IoC ioc;

    @Inject
    private ClassGenerator classGenerator;

    @Inject
    private ResourceLoader resourceLoader;

    @PostProcess(priority = 2)
    public void initEngine() {
        try {
            FileUtils.createDirectoryIfNotExists(scripts_root);
            loadNashorn();
            if (engine != null) {
                setup();
                reloadGlobalEffects();
                reloadSkills();
                reloadAttributes();
                generateListener();
                System.out.println("JS resources loaded.");
            } else {
                logger.error("Could not load nashorn. Library not found on a classpath.");
                logger.error(" - For SpongeVanilla create a symlink or place nashorn.jar into the sponge/config/nt-core folder");
                logger.error(" - For SpongeForge create a symlink or place nashorn.jar into the sponge/mods folder");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    public void loadNashorn() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    private void setup() {
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
            bindings.put("IoC", ioc);
            bindings.put("Bindings", new BindingsHelper(engine));
            bindings.put("Folder",scripts_root.toString());
            bindings.put("GlobalScope", ioc.build(GlobalScope.class));
            engine.setBindings(bindings,ScriptContext.ENGINE_SCOPE);
            engine.eval(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateDynamicListener(Map<StaticClass, Set<Consumer<? extends Event>>> set) {
        Object o = classGenerator.generateDynamicListener(set);
        ioc.build(Game.class).getEventManager().registerListeners(ioc.build(NtRpgPlugin.class), o);
    }

    public void reloadSkills() {
        Invocable invocable = (Invocable) engine;
        try {
            invocable.invokeFunction("registerSkills");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void reloadGlobalEffects() {
        Invocable invocable = (Invocable) engine;
        try {
            invocable.invokeFunction("registerGlobalEffects");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void reloadAttributes() {
        Invocable invocable = (Invocable) engine;
        try {
            invocable.invokeFunction("registerAttributes");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void generateListener() {
        Invocable invocable = (Invocable) engine;
        try {
            invocable.invokeFunction("generateListener");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static class BindingsHelper {

        private ScriptEngine scriptEngine;

        public BindingsHelper(ScriptEngine scriptEngine) {
            this.scriptEngine = scriptEngine;
        }

        public ScriptEngine getScriptEngine() {
            return scriptEngine;
        }

        public Set<Map.Entry<String, Object>> getEngineScopeKeys() {
            return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).entrySet();
        }

        public Bindings getGlobalScopeKeys() {
            return (Bindings) scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).entrySet();
        }
    }

}

