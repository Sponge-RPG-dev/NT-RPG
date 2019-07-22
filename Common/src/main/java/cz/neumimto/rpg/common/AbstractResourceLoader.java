package cz.neumimto.rpg.common;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.effects.model.EffectModelMapper;
import cz.neumimto.rpg.api.localization.Localization;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.properties.PropertyContainer;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.utils.ResourceClassLoader;

import javax.inject.Inject;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

public class AbstractResourceLoader implements IResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";
    public static File classDir, addonDir, skilltreeDir, addonLoadDir, localizations;



    @Inject
    private SkillService skillService;

    @Inject
    private ClassService classService;

    @Inject
    private IEffectService effectService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private ClassGenerator classGenerator;

    @Inject
    private LocalizationService localizationService;

    @Inject
    protected IScriptEngine jsEngine;

    private Map<String, URLClassLoader> classLoaderMap = new HashMap<>();

    private URLClassLoader configClassLaoder;

    @Inject
    protected Injector injector;

    private static boolean reload = false;

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
        Path dir = Paths.get(Rpg.get().getWorkingDirectory()).resolve("addons");
        for (File f : dir.toFile().listFiles()) {
            loadJarFile(f, false);
        }
        reload = true;
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
            prepareClassLoader(f);
        }
        ClassLoader classLoader = null;
        if (main) {
            classLoader = Rpg.get().getClass().getClassLoader();
        } else {
            classLoader = getClassLoaderMap().get(f.getName());
        }

        Set<Pair<File, String>> localizationsToLoad = new HashSet<>();
        Set<String> classesToLoad = new HashSet<>();

        while (entries.hasMoreElements()) {
            next = entries.nextElement();
            if (next.isDirectory() || !next.getName().endsWith(".class")) {
                if (next.getName().contains("localizations") && next.getName().endsWith(".properties")) {
                    String name = next.getName();
                    int i = name.lastIndexOf("/");
                    String substring = name.substring(i);
                    File file1 = new File(Rpg.get().getWorkingDirectory(), substring);
                    localizationsToLoad.add(new Pair<>(file1, name));
                }
                if (reload && next.getName().contains("ntrpg-unreloadable")) {
                    return;
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
            classesToLoad.add(next.getName());
        }

        for (Pair<File, String> pair : localizationsToLoad) {
            loadLocalizationPropertiesFiles(classLoader, pair.value, pair.key);
        }

        for (String classToLoad : classesToLoad) {
            loadClass(main, classLoader, classToLoad);
        }
        info("Finished loading of jarfile " + file.getName());

    }

    protected void loadClass(boolean main, ClassLoader classLoader, String classToLoad) {
        String className = classToLoad.substring(0, classToLoad.length() - 6);
        className = className.replace('/', '.');
        Class<?> clazz = null;
        try {
            if (!main) {
                clazz = classLoader.loadClass(className);
                if (loadClass(clazz) != null) {
                    info("ClassLoader for "
                                    + Console.GREEN_BOLD + classLoader +
                                    Console.RESET + " loaded class " +
                                    Console.GREEN + clazz.getSimpleName() + Console.RESET,
                            Rpg.get().getPluginConfig().DEBUG);
                }
            } else {
                clazz = Class.forName(className);
                loadClass(clazz);
            }
        } catch (Exception e) {
            error("Could not load the class [" + className + "]" + e.getMessage(), e);
        }
    }

    protected void prepareClassLoader(File f) {
        URLClassLoader classLoader = getClassLoaderMap().get(f.getName());
        if (classLoader == null) {

            try {
                classLoader = new ResourceClassLoader(f.toPath().getFileName().toString().trim(),
                        new URL[]{f.toURI().toURL()},
                        Rpg.get().getClass().getClassLoader());

                getClassLoaderMap().put(f.getName(), classLoader);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadLocalizationPropertiesFiles(ClassLoader classLoader, String name, File file) {
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(name)) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);

            if (!file.exists()) {
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    properties.store(outputStream, null);
                }
            } else {
                Properties local = new Properties();
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    properties.load(fileInputStream);
                }
                properties.putAll(local);
                file.delete();
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    properties.store(outputStream, null);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (clazz.isAnnotationPresent(Skill.class)) {
            container = loadSkillClass(clazz);
        }
        if (clazz.isAnnotationPresent(PropertyContainer.class)) {
            loadPropertyContainerClass(clazz);
        }
        if (clazz.isAnnotationPresent(JsBinding.class)) {
            loadScriptBindingsClass(clazz);
        }
        if (clazz.isAnnotationPresent(Localization.class)) {
            loadLocalizationBindingsClass(clazz);
        }
        if (IGlobalEffect.class.isAssignableFrom(clazz)) {
            container = loadIGlobalEffectClass(clazz);
        }
        if (clazz.isAnnotationPresent(ModelMapper.class)) {
            loadModelMapperClass(clazz);
        }
        return container;
    }

    protected void loadModelMapperClass(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        EffectModelMapper o = (EffectModelMapper) clazz.newInstance();
        EffectModelFactory.getTypeMappers().put(o.getType(), o);
    }

    protected Object loadIGlobalEffectClass(Class<?> clazz) {
        Object container;
        container = newInstance(IGlobalEffect.class, clazz);
        effectService.registerGlobalEffect((IGlobalEffect) container);
        return container;
    }

    protected void loadLocalizationBindingsClass(Class<?> clazz) {
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

    protected void loadScriptBindingsClass(Class<?> clazz) {
        jsEngine.getDataToBind().put(clazz, clazz.getAnnotation(JsBinding.class).value());
    }

    protected void loadPropertyContainerClass(Class<?> clazz) {
        DebugLevel debugLevel = Rpg.get().getPluginConfig().DEBUG;
        info("Found Property container class" + clazz.getName(), debugLevel);
        propertyService.processContainer(clazz);
    }

    protected Object loadSkillClass(Class<?> clazz) {
        DebugLevel debugLevel = Rpg.get().getPluginConfig().DEBUG;
        Object container;
        container = injector.getInstance(clazz);
        info("registering skill " + clazz.getName(), debugLevel);
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
        return container;
    }

    @Override
    public URLClassLoader getConfigClassLoader() {
        return configClassLaoder;
    }

    public Map<String, URLClassLoader> getClassLoaderMap() {
        return classLoaderMap;
    }

}
