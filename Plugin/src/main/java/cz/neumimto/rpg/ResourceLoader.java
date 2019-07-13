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
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.common.utils.ResourceClassLoader;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.CommandBase;
import cz.neumimto.rpg.sponge.commands.CommandService;
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Game;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@SuppressWarnings("unchecked")
@Singleton
public class ResourceLoader extends IResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";

    public static File classDir, addonDir, skilltreeDir, addonLoadDir, localizations;

    private static URLClassLoader localizationsClassLoader;

    static {
        classDir = new File(NtRpgPlugin.workingDir + File.separator + "classes");
        addonDir = new File(NtRpgPlugin.workingDir + File.separator + "addons");
        addonLoadDir = new File(NtRpgPlugin.workingDir + File.separator + ".deployed");
        skilltreeDir = new File(NtRpgPlugin.workingDir + File.separator + "Skilltrees");
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
    private SkillService skillService;

    @Inject
    private ClassService classService;

    @Inject
    private IEffectService effectService;

    @Inject
    private PropertyService propertyService;

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
                    loadClass(clazz);
                } else {
                    clazz = Class.forName(className);
                    loadClass(clazz);
                }
            } catch (Exception e) {
                error("Could not load the class [" + className + "]" + e.getMessage(), e);
            }
        }
        info("Finished loading of jarfile " + file.getName());
    }

    @Override
    public Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException {
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
            if (skill.getLocalizableName() == null || skill.getLocalizableName().isEmpty()) {
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
            propertyService.processContainer(clazz);
        }
        if (clazz.isAnnotationPresent(JsBinding.class)) {
            jsLoader.getDataToBind().put(clazz, clazz.getAnnotation(JsBinding.class).value());
        }
        if (clazz.isAnnotationPresent(Localization.class)) {
            Localization annotation = clazz.getAnnotation(Localization.class);
            File localizations = new File(Rpg.get().getWorkingDirectory() + "/localizations");
            if (!localizations.exists()) {
                localizations.mkdir();
            }

            for (String localizationFile : annotation.value()) {
                try (InputStream resourceAsStream = clazz.getClassLoader().getResourceAsStream(localizationFile)) {
                    byte[] buffer = new byte[resourceAsStream.available()];
                    resourceAsStream.read(buffer);
                    String[] split = localizationFile.split("/");
                    File targetFile = new File(localizations, split[split.length - 1]);
                    OutputStream outStream = new FileOutputStream(targetFile);
                    outStream.write(buffer);
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        if (clazz.isAnnotationPresent(Repository.class)) {
            container = injector.getInstance(clazz);
            PluginCore.Instance.injectPersistentContext(container);
        }
        return container;
    }

    @Override
    public URLClassLoader getConfigClassLoader() {
        return configClassLaoder;
    }

    public Map<String, URLClassLoader> getClassLoaderMap() {
        return Collections.unmodifiableMap(classLoaderMap);
    }

    public void reloadLocalizations(Locale locale) {
        File localizations = new File(Rpg.get().getWorkingDirectory() + "/localizations");
        String language = locale.getLanguage();
        Log.info("Loading localization from language " + language);
        File[] files = localizations.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(language + ".properties")) {
                Log.info("Loading localization from file " + file.getName());
                try (FileInputStream input = new FileInputStream(file)) {
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
                    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                        if (entry.getValue() != null && !((String) entry.getValue()).isEmpty()) {
                            localizationService.addTranslationKey(entry.getKey().toString(), entry.getValue().toString());
                        }
                    }

                } catch (IOException e) {
                    Log.error("Could not read localization file " + file.getName(), e);
                }
            }
        }
    }

}
