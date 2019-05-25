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

import com.google.inject.Injector;
import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.configuration.ConfigurationContainer;
import cz.neumimto.core.PluginCore;
import cz.neumimto.core.Repository;
import cz.neumimto.core.localization.Localization;
import cz.neumimto.core.localization.ResourceBundle;
import cz.neumimto.core.localization.ResourceBundles;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.properties.PropertyContainer;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.effects.model.EffectModelMapper;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.CommandBase;
import cz.neumimto.rpg.sponge.commands.CommandService;
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Game;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@SuppressWarnings("unchecked")
@Singleton
public class ResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";

    public static File classDir, addonDir, skilltreeDir, addonLoadDir, localizations;

    private static URLClassLoader localizationsClassLoader;

    static {
        classDir = new File(NtRpgPlugin.workingDir + File.separator + "classes");
        addonDir = new File(NtRpgPlugin.workingDir + File.separator + "addons");
        addonLoadDir = new File(NtRpgPlugin.workingDir + File.separator + ".deployed");
        skilltreeDir = new File(NtRpgPlugin.workingDir + File.separator + "skilltrees");
        localizations = new File(NtRpgPlugin.workingDir + File.separator + "localizations");
        classDir.mkdirs();
        skilltreeDir.mkdirs();
        addonDir.mkdirs();
        localizations.mkdirs();
        try {
            localizationsClassLoader = new URLClassLoader(new URL[]{localizations.toURI().toURL()});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.deleteDirectory(addonLoadDir);
            FileUtils.copyDirectory(addonDir, addonLoadDir, pathname -> pathname.isDirectory() || pathname.getName().endsWith(".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    private ISkillService skillService;

    @Inject
    private ClassService classService;

    @Inject
    private EffectService effectService;

    @Inject
    private SpongePropertyService spongePropertyService;

    @Inject
    private CommandService commandService;

    private ConfigMapper configMapper;

    @Inject
    private ClassGenerator classGenerator;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private Injector injector;

    @Inject
    private Game game;

    @Inject
    private JSLoader jsLoader;

    private Map<String, URLClassLoader> classLoaderMap = new HashMap<>();

    private URLClassLoader configClassLaoder;

    private Set<String> resourceBundles = new HashSet<>();

    public ResourceLoader() {
        ConfigMapper.init("NtRPG", Paths.get(NtRpgPlugin.workingDir));
        configMapper = ConfigMapper.get("NtRPG");
        configClassLaoder = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader());
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
        Path dir = addonLoadDir.toPath();
        for (File f : dir.toFile().listFiles()) {
            loadJarFile(f, false);
        }
    }

    public void loadJarFile(File f, boolean main) {
        if (f == null) {
            return;
        }
        JarFile file = null;
        try {
            file = new JarFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        info("Loading jarfile " + file.getName());
        Enumeration<JarEntry> entries = file.entries();
        JarEntry next = null;

        if (!main) {
            URLClassLoader classLoader = classLoaderMap.get(f.getName());
            if (classLoader == null) {

                try {
                    classLoader = new ResourceClassLoader(f.toPath().getFileName().toString().trim(),
                            new URL[]{f.toURI().toURL()},
                            PluginCore.getClassLoader());

                    classLoaderMap.put(f.getName(), classLoader);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        ClassLoader classLoader = null;
        if (main) {
            classLoader = NtRpgPlugin.class.getClassLoader();
        } else {
            classLoader = classLoaderMap.get(f.getName());
        }
        while (entries.hasMoreElements()) {
            next = entries.nextElement();
            if (next.isDirectory() || !next.getName().endsWith(".class")) {
                if (next.getName().contains("localizations") && next.getName().endsWith(".properties")) {
                    String name = next.getName();
                    int i = name.lastIndexOf("/");
                    String substring = name.substring(i);
                    File file1 = new File(localizations, substring);

                    try (InputStream resourceAsStream = classLoader.getResourceAsStream(name)) {
                        Properties properties = new Properties();
                        properties.load(resourceAsStream);

                        if (!file1.exists()) {
                            try (OutputStream outputStream = new FileOutputStream(file1)) {
                                properties.store(outputStream, null);
                            }
                        } else {
                            Properties local = new Properties();
                            try (FileInputStream fileInputStream = new FileInputStream(file1)) {
                                properties.load(fileInputStream);
                            }
                            properties.putAll(local);
                            file1.delete();
                            try (OutputStream outputStream = new FileOutputStream(file1)) {
                                properties.store(outputStream, null);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
            if (main && !next.getName().startsWith("cz/neumimto")) {
                continue;
            }
            //todo place this into each modules
            if (next.getName().startsWith("org")
                    || next.getName().startsWith("javax")) {
                continue;
            }
            if (next.getName().lastIndexOf(INNERCLASS_SEPARATOR) > 1) {
                continue;
            }
            String className = next.getName().substring(0, next.getName().length() - 6);
            className = className.replace('/', '.');
            Class<?> clazz = null;
            try {
                if (!main) {
                    clazz = classLoader.loadClass(className);
                    info("ClassLoader for "
                            + cz.neumimto.rpg.api.utils.Console.GREEN_BOLD + classLoader +
                            cz.neumimto.rpg.api.utils.Console.RESET + " loaded class " +
                            cz.neumimto.rpg.api.utils.Console.GREEN + clazz.getSimpleName() + Console.RESET, pluginConfig
                            .DEBUG);
                    loadClass(clazz, classLoader);
                } else {
                    clazz = Class.forName(className);
                    loadClass(clazz, this.getClass().getClassLoader());
                }
            } catch (Exception e) {
                error("Could not load the class [" + className + "]" + e.getMessage(), e);
            }
        }
        info("Finished loading of jarfile " + file.getName());
    }

    public Object loadClass(Class<?> clazz, ClassLoader classLoader) throws IllegalAccessException, InstantiationException {
        //Properties
        if (clazz == IGlobalEffect.class) {
            return null;
        }
        Object container = null;
        if (clazz.isInterface() && clazz.getAnnotations().length == 0) {
            return null;
        }
        if (clazz.isAnnotationPresent(ListenerClass.class)) {
            info("Registering listener class" + clazz.getName(), pluginConfig.DEBUG);
            container = injector.getInstance(clazz);
            game.getEventManager().registerListeners(NtRpgPlugin.GlobalScope.plugin, container);
        }
        if (clazz.isAnnotationPresent(Command.class)) {
            container = injector.getInstance(clazz);
            info("registering command class" + clazz.getName(), pluginConfig.DEBUG);
            commandService.registerCommand((CommandBase) container);
        }
        if (clazz.isAnnotationPresent(Skill.class)) {
            container = injector.getInstance(clazz);
            info("registering skill " + clazz.getName(), pluginConfig.DEBUG);
            ISkill skill = (ISkill) container;
            Skill sk = clazz.getAnnotation(Skill.class);
            if (sk.dynamicLocalizationNodes()) {
                String[] split = sk.value().split(":");
                String key = split[0] + ".skills." + split[1];
                skill.setLocalizableName(localizationService.translate(key + ".name"));
                skill.setDescription(localizationService.translateMultiline(key + ".desc"));
                skill.setLore(localizationService.translateMultiline(key + ".lore"));
            }
            if (skill.getLocalizableName().isEmpty()) {
                String name = skill.getClass().getSimpleName();
                name = name.startsWith("Skill") ? name.substring("Skill".length()) : name;
                skill.setLocalizableName(name);
            }
            skillService.registerAdditionalCatalog(skill);
        }
        if (clazz.isAnnotationPresent(ConfigurationContainer.class)) {
            configMapper.loadClass(clazz);
            info("Found configuration container class " + clazz.getName(), pluginConfig.DEBUG);
        }
        if (clazz.isAnnotationPresent(PropertyContainer.class)) {
            info("Found Property container class" + clazz.getName(), pluginConfig.DEBUG);
            spongePropertyService.processContainer(clazz);
        }
        if (clazz.isAnnotationPresent(JsBinding.class)) {
            jsLoader.getDataToBind().put(clazz, clazz.getAnnotation(JsBinding.class).value());
        }
        if (clazz.isAnnotationPresent(ResourceBundles.class)) {
            ResourceBundles annotation = clazz.getAnnotation(ResourceBundles.class);
            File localizations = new File(NtRpgPlugin.workingDir + "/localizations");
            if (!localizations.exists()) {
                localizations.mkdir();
            }
            for (ResourceBundle resourceBundle : annotation.value()) {
                resourceBundles.add(resourceBundle.value());
            }
        }
        if (IGlobalEffect.class.isAssignableFrom(clazz)) {
            container = newInstance(IGlobalEffect.class, clazz);
            effectService.registerGlobalEffect((IGlobalEffect) container);
        }
        if (clazz.isAnnotationPresent(ModelMapper.class)) {
            EffectModelMapper o = (EffectModelMapper) clazz.newInstance();
            EffectModelFactory.typeMappers.put(o.getType(), o);
        }
        if (clazz.isAnnotationPresent(Localization.class)) {
            localizationService.registerClass(clazz);
        }
        if (clazz.isAnnotationPresent(Repository.class)) {
            container = injector.getInstance(clazz);
            PluginCore.Instance.injectPersistentContext(container);
        }
        return container;
    }

    public URLClassLoader getConfigClassLoader() {
        return configClassLaoder;
    }

    public Map<String, URLClassLoader> getClassLoaderMap() {
        return Collections.unmodifiableMap(classLoaderMap);
    }

    public void reloadLocalizations(Locale locale) {
        for (String resourceBundle : resourceBundles) {
            localizationService.loadResourceBundle(resourceBundle, locale, localizationsClassLoader);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ListenerClass {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Skill {

        String value();

        boolean dynamicLocalizationNodes() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModelMapper {

    }
}
