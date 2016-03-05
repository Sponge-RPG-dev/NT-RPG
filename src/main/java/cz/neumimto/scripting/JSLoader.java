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

package cz.neumimto.scripting;

import cz.neumimto.ClassGenerator;
import cz.neumimto.GlobalScope;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.utils.FileUtils;
import javassist.CannotCompileException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.spongepowered.api.event.Event;

import javax.script.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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
            Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
            FileUtils.createDirectoryIfNotExists(scripts_root);
            engine = new NashornScriptEngineFactory().getScriptEngine();
            loadSkills();
        } catch (ClassNotFoundException e) {
            System.out.println("Nashorn was not loaded. To enable javascript support place nashorn.jar into your mods folder");
        }
    }

    public void loadSkills() {
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
            bindings.put("logger", ioc.build(Logger.class));
            bindings.put("ioc", ioc);
            bindings.put("GlobalScope", ioc.build(GlobalScope.class));
            engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            engine.eval(rs);
        } catch (ScriptException | IOException e) {
            e.printStackTrace();
        }
    }

    public void a(Set<Class<? extends Event>> set) {
        Class<?> c = classGenerator.generateDynamicListener(set);
        try {
            resourceLoader.loadClass(c);
        } catch (IllegalAccessException | CannotCompileException | InstantiationException e) {
            e.printStackTrace();
        }
    }

}

