package cz.neumimto.rpg.common;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.effects.model.EffectModelMapper;
import cz.neumimto.rpg.api.localization.Localization;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.properties.PropertyContainer;
import cz.neumimto.rpg.api.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.utils.ResourceClassLoader;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

@Singleton
public class ResourceManagerImpl implements ResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";

    public static File classDir, addonDir, skilltreeDir, addonLoadDir, localizations;
    private static boolean reload = false;
    private static Set<Class> classesToLoad = new HashSet<>();

    @Inject
    protected IRpgScriptEngine jsEngine;
    @Inject
    protected Injector injector;
    @Inject
    private SkillService skillService;
    @Inject
    private ClassService classService;
    @Inject
    private EffectService effectService;
    @Inject
    private PropertyServiceImpl propertyService;
    @Inject
    private ClassGenerator classGenerator;
    @Inject
    private LocalizationService localizationService;
    private Map<String, URLClassLoader> classLoaderMap = new HashMap<>();
    private URLClassLoader configClassLaoder;

    private static <T> T newInstance(Class<T> excepted, Class<?> clazz) {
        T t = null;
        try {
            t = (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static Set<RpgAddon> discoverGuiceModules() {
        Set<RpgAddon> addons = new HashSet<>();

        for (Class aClass : AddonScanner.getClassesToLoad()) {
            if (RpgAddon.class.isAssignableFrom(aClass)
                    && !aClass.isInterface()
                    && !Modifier.isAbstract(aClass.getModifiers())) {
                try {
                    addons.add((RpgAddon) aClass.getConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    Log.error("Could not call a default constructor on a class " + aClass.getName());
                }
            }
            classesToLoad.add(aClass);
        }

        return addons;
    }

    @Override
    public void init() {
        String workingDirectory = Rpg.get().getWorkingDirectory();
        classDir = new File(workingDirectory + File.separator + "classes");
        addonDir = new File(workingDirectory + File.separator + "addons");
        addonLoadDir = new File(workingDirectory + File.separator + "deployed");
        skilltreeDir = new File(workingDirectory + File.separator + "Skilltrees");
        localizations = new File(workingDirectory + File.separator + "localizations");
        classDir.mkdirs();
        skilltreeDir.mkdirs();
        addonDir.mkdirs();
        localizations.mkdirs();


    }

    @Override
    public void loadJarFile(File f, boolean main) {
        if (f == null) {
            return;
        }
        JarFile file;
        try {
            file = new JarFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        info("Loading jarfile " + file.getName());

        if (!main) {
            prepareClassLoader(f);
        }

        ClassLoader classLoader;
        if (main) {
            classLoader = Rpg.get().getClass().getClassLoader();
        } else {
            classLoader = getClassLoaderMap().get(f.getName());
        }

        Set<Pair<File, String>> localizationsToLoad = new HashSet<>();
        Set<String> classesToLoad = new HashSet<>();

        JarEntry next;
        Enumeration<JarEntry> entries = file.entries();
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
            if (next.getName().startsWith("org") || next.getName().startsWith("javax")) {
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
            if (classToLoad.startsWith("cz/neumimto/rpg/spigot/bridges")) {
                continue;
            }
            Class<?> clazz = loadClass(main, classLoader, classToLoad);
            ResourceManagerImpl.classesToLoad.add(clazz);
        }

        info("Finished parsing of jarfile " + file.getName());
    }

    protected Class<?> loadClass(boolean main, ClassLoader classLoader, String classToLoad) {
        String className = classToLoad.substring(0, classToLoad.length() - 6);
        className = className.replace('/', '.');
        Class<?> clazz = null;
        try {
            if (!main) {
                clazz = classLoader.loadClass(className);
            } else {
                clazz = Class.forName(className);
            }
        } catch (Throwable e) {
            error("Could not load the class [" + className + "]" + e.getMessage(), e);
        }
        return clazz;
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
    public Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException, ConfigurationException {
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
        if (clazz.isAnnotationPresent(ListenerClass.class)) {
            info("Registering listener class" + clazz.getName(), Rpg.get().getPluginConfig().DEBUG);
            container = injector.getInstance(clazz);
            Rpg.get().registerListeners(container);
        }
        if (clazz.isAnnotationPresent(Singleton.class) && hasDefaultCtr(clazz)) {
            container = injector.getInstance(clazz);
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
        skillService.registerAdditionalCatalog(skill);
        return container;
    }

    @Override
    public URLClassLoader getConfigClassLoader() {
        return configClassLaoder;
    }

    @Override
    public Map<String, URLClassLoader> getClassLoaderMap() {
        return classLoaderMap;
    }

    @Override
    public void reloadLocalizations(Locale locale) {
        loadLocalizationsFromClasspath("localizations/core_localization_en.properties");
        loadLocalizationsFromClasspath("localizations/core_localization_" + locale.getLanguage() + ".properties");

        File localizations = new File(Rpg.get().getWorkingDirectory() + "/localizations");
        String language = locale.getLanguage();
        Log.info("Loading localization from language " + language);
        File[] files = localizations.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(language + ".properties")) {
                Log.info("Loading localization from file " + file.getName());
                try (FileInputStream input = new FileInputStream(file)) {
                    loadLocalizations(input);
                } catch (IOException e) {
                    Log.error("Could not read localization file " + file.getName(), e);
                }
            }
        }
    }

    protected void loadLocalizationsFromClasspath(String classpathLoc) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathLoc)) {
            loadLocalizations(is);
        } catch (IOException e) {
            Log.error("Could not read localization file classpath: " + classpathLoc, e);
        }
    }

    protected void loadLocalizations(InputStream input) throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null && !((String) entry.getValue()).isEmpty()) {
                localizationService.addTranslationKey(entry.getKey().toString(), entry.getValue().toString());
            }
        }
    }

    @Override
    public void loadExternalJars() {
        try {
            FileUtils.deleteDirectory(addonLoadDir);
            addonLoadDir.mkdir();
            FileUtils.copyDirectory(addonDir, addonLoadDir, pathname -> pathname.isDirectory() || pathname.getName().endsWith(".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : addonLoadDir.listFiles()) {
            loadJarFile(file, false);
        }
    }

    @Override
    public void initializeComponents() {
        for (Class aClass : classesToLoad) {
            initializeComponent(aClass);
        }
        classesToLoad.clear();
    }

    public void initializeComponent(Class<?> clazz) {
        try {
            if (loadClass(clazz) != null) {
                info("ClassLoader for "
                                + Console.GREEN_BOLD + clazz.getClassLoader() +
                                Console.RESET + " loaded class " +
                                Console.GREEN + clazz.getSimpleName() + Console.RESET,
                        Rpg.get().getPluginConfig().DEBUG);
            }
        } catch (IllegalAccessException | InstantiationException | ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private boolean hasDefaultCtr(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }
}
