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

package cz.neumimto.rpg;

import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.configuration.ConfigurationContainer;
import cz.neumimto.core.PluginCore;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.commands.CommandBase;
import cz.neumimto.rpg.commands.CommandService;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.players.properties.PropertyContainer;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import javassist.CannotCompileException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@Singleton
public class ResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";

    //TODO use nio instead of io
    public static File classDir, raceDir, guildsDir, addonDir, skilltreeDir;

    private static IoC ioc;

    static {
        classDir = new File(NtRpgPlugin.workingDir + File.separator + "classes");
        raceDir = new File(NtRpgPlugin.workingDir + File.separator + "races");
        guildsDir = new File(NtRpgPlugin.workingDir + File.separator + "guilds");
        addonDir = new File(NtRpgPlugin.workingDir + File.separator + "addons");
        skilltreeDir = new File(NtRpgPlugin.workingDir + File.separator + "skilltrees");
        classDir.mkdirs();
        raceDir.mkdirs();
        guildsDir.mkdirs();
        skilltreeDir.mkdirs();
        addonDir.mkdirs();
        ioc = IoC.get();
    }

    @Inject
    private SkillService skillService;
    @Inject
    private GroupService groupService;
    @Inject
    private EffectService effectService;
    @Inject
    private PropertyService propertyService;
    @Inject
    private Logger logger;
    @Inject
    private CommandService commandService;
    private ConfigMapper configMapper;
    @Inject
    private ClassGenerator classGenerator;

    public ResourceLoader() {
        ConfigMapper.init("NtRPG", Paths.get(NtRpgPlugin.workingDir));
        configMapper = ConfigMapper.get("NtRPG");
    }

    private static <T> T newInstance(Class<T> excepted, Class<?> clazz) {
        T t = null;
        try {
            t = (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    public void loadExternalJars() {
        Path dir = addonDir.toPath();
        for (File f : dir.toFile().listFiles()) {
            loadJarFile(f, false);
        }
    }

    public void loadJarFile(File f, boolean main) {
        if (f == null)
            return;
        JarFile file = null;
        try {
            file = new JarFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Loading jarfile " + file.getName());
        Enumeration<JarEntry> entries = file.entries();
        JarEntry next = null;

        if (!main) {
            PluginCore.loadJarFile(f);
        }
        while (entries.hasMoreElements()) {
            next = entries.nextElement();
            if (next.isDirectory() || !next.getName().endsWith(".class")) {
                continue;
            }
            if (main && !next.getName().startsWith("cz/neumimto"))
                continue;
            //todo place this into each modules
            if (next.getName().startsWith("org")
                    || next.getName().startsWith("spark")
                    || next.getName().startsWith("javax")) {
                continue;
            }
            if (next.getName().lastIndexOf(INNERCLASS_SEPARATOR) > 1)
                continue;
            String className = next.getName().substring(0, next.getName().length() - 6);
            className = className.replace('/', '.');
            Class<?> clazz = null;
            try {
                clazz = PluginCore.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
            try {
                loadClass(clazz);
            } catch (IllegalAccessException | CannotCompileException | InstantiationException e) {
                e.printStackTrace();
            }

        }
        logger.info("Finished loading of jarfile " + file.getName());
    }

    public void loadClass(Class<?> clazz) throws IllegalAccessException, CannotCompileException, InstantiationException {
        if (clazz.isInterface())
            return;
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }
        if (PluginConfig.DEBUG)
            logger.info(" - Checking if theres something to load in a class " + clazz.getName());
        //Properties
        Object container = null;
        if (clazz.isAnnotationPresent(Singleton.class)) {
            ioc.build(clazz);
        }
        if (clazz.isAnnotationPresent(ListenerClass.class)) {
            if (PluginConfig.DEBUG)
                logger.info("Registering listener" + clazz.getName());
            container = ioc.build(clazz);
            ioc.build(Game.class).getEventManager().registerListeners(ioc.build(NtRpgPlugin.class), container);
        }
        if (clazz.isAnnotationPresent(Command.class)) {
            container = ioc.build(clazz);
            if (PluginConfig.DEBUG)
                logger.info("registering command class" + clazz.getName());
            commandService.registerCommand((CommandBase) container);
        }
        if (clazz.isAnnotationPresent(Skill.class)) {
            container = ioc.build(clazz);
            if (PluginConfig.DEBUG)
                logger.info("registering skill " + clazz.getName());
            skillService.addSkill((ISkill) container);
        }
        if (clazz.isAnnotationPresent(ConfigurationContainer.class)) {
            configMapper.loadClass(clazz);
            if (PluginConfig.DEBUG)
                logger.info("Found configuration container class", clazz.getName());
        }
        if (clazz.isAnnotationPresent(PropertyContainer.class)) {
            if (PluginConfig.DEBUG)
                logger.info("Found Property container class" + clazz.getName());
            propertyService.process(clazz);
        }
        if (clazz.isAnnotationPresent(Attribute.class)) {
            propertyService.registerAttribute((ICharacterAttribute) clazz.newInstance());
        }
        //Effects
        if (IEffect.class.isAssignableFrom(clazz)) {
            ClassGenerator.Generate a = clazz.getAnnotation(ClassGenerator.Generate.class);
            if (a != null) {
                Class c = (Class<? extends IEffect>) clazz;
                IGlobalEffect iGlobalEffect = classGenerator.generateGlobalEffect(c);
                if (iGlobalEffect == null) {
                    return;
                }
                classGenerator.injectGlobalEffectField(c, iGlobalEffect);
                effectService.registerGlobalEffect(iGlobalEffect);
            }
        }
        if (IGlobalEffect.class.isAssignableFrom(clazz)) {
            IGlobalEffect i = newInstance(IGlobalEffect.class, clazz);
            effectService.registerGlobalEffect(i);
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ListenerClass {
    }


    @Retention(RetentionPolicy.RUNTIME)
    public @interface Skill {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Attribute {
    }
}
