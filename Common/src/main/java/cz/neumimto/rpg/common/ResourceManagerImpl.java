package cz.neumimto.rpg.common;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.effects.model.EffectModelMapper;
import cz.neumimto.rpg.api.localization.Localization;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.api.services.ILocalization;
import cz.neumimto.rpg.api.services.IPropertyContainer;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static cz.neumimto.rpg.api.logging.Log.info;

public class ResourceManagerImpl implements ResourceLoader {

    private final static String INNERCLASS_SEPARATOR = "$";

    public static File classDir, addonDir, skilltreeDir, addonLoadDir, localizations;
    private static boolean reload = false;

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

    @Override
    public void init() {
        String workingDirectory = Rpg.get().getWorkingDirectory();
        classDir = new File(workingDirectory + File.separator + "classes");
        addonDir = new File(workingDirectory + File.separator + "addons");
        addonLoadDir = new File(workingDirectory + File.separator + "deployed");
        skilltreeDir = new File(workingDirectory + File.separator + "skilltrees");
        localizations = new File(workingDirectory + File.separator + "localizations");

        classDir.mkdirs();
        skilltreeDir.mkdirs();
        addonDir.mkdirs();
        localizations.mkdirs();
    }

    @Override
    public void loadServices() {
        load(ISkill.class, getClass().getClassLoader()).forEach(a -> skillService.registerAdditionalCatalog(a));
        load(IPropertyContainer.class, getClass().getClassLoader()).forEach(a -> loadPropertyContainerClass(a.getClass()));
        load(IGlobalEffect.class, getClass().getClassLoader()).forEach(a -> effectService.registerGlobalEffect(a));
        load(EffectModelMapper.class, getClass().getClassLoader()).forEach(a -> EffectModelFactory.getTypeMappers().put(a.getType(), a));
        load(ILocalization.class, getClass().getClassLoader()).forEach(a -> loadLocalizationBindingsClass(a.getClass()));
    }

    protected <R> Stream<R> load(Class<R> r, ClassLoader cl) {
        return ServiceLoader.load(r, cl).stream().map(ServiceLoader.Provider::get);
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

    protected void loadPropertyContainerClass(Class<?> clazz) {
        DebugLevel debugLevel = Rpg.get().getPluginConfig().DEBUG;
        info("Found Property container class" + clazz.getName(), debugLevel);
        propertyService.processContainer(clazz);
    }

    @Override
    public ISkill loadSkillClass(Class<? extends ISkill> clazz) {
        ISkill container = injector.getInstance(clazz);
        skillService.registerAdditionalCatalog(container);
        return container;
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
        FileUtils.deleteDirectory(addonLoadDir);
        addonLoadDir.mkdir();
        FileUtils.copyDirectory(addonDir, addonLoadDir, path -> path.endsWith(".jar"));
    }

}
