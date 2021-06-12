package cz.neumimto.rpg.common;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;
import dev.xdark.deencapsulation.Deencapsulation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonScanner {

    private final static String INNERCLASS_SEPARATOR = "$";

    private static boolean stage;

    private static Path addonDir;

    private static Set<Class<?>> annotations = new HashSet<>();
    private static Set<Class<?>> classesToLoad = new HashSet<>();
    private static Set<String> exclusions = new HashSet<>();

    private static ClassLoader classLoader;
    private static ModuleLayer moduleLayer;

    private static Method addUrl;
    static {
        Deencapsulation.deencapsulate(ClassLoader.class);
        URLClassLoader loader = (URLClassLoader) AddonScanner.class.getClassLoader();
        try {
            addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrl.setAccessible(true);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        exclusions.add("com.ibm.icu");
        exclusions.add("com.oracle");

        annotations.add(ResourceLoader.Skill.class);
        annotations.add(ResourceLoader.Command.class);
        annotations.add(ResourceLoader.ModelMapper.class);
        annotations.add(ResourceLoader.ListenerClass.class);
    }

    public static void setAddonDir(Path addonDir) {
        AddonScanner.addonDir = addonDir;
        if (!Files.exists(addonDir)) {
            try {
                Files.createDirectory(addonDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onlyReloads() {
        AddonScanner.stage = true;
    }

    public static Set<Class<?>> getClassesToLoad() {
        Set<Class<?>> classes = new HashSet<>(classesToLoad);
        classesToLoad.clear();
        return classes;
    }

    public static void prepareAddons() {
        if (!stage) {
            findRelevantClassCandidatesInPath();
        }
    }

    private static void findRelevantClassCandidatesInPath() {
        moduleLayer = addToClassPath(addonDir);

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(addonDir, "*.jar")) {
            paths.forEach(AddonScanner::visitJarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void visitJarFile(Path path) {
        try {
            JarFile jarFile = new JarFile(path.toFile());
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String s = getValidClassName(jarEntry);
                if (s == null) {
                    continue;
                }
                if (s.lastIndexOf(INNERCLASS_SEPARATOR) > 1) {
                    continue;
                }
                if (skip(s)) {
                    continue;
                }

                try {
                    moduleLayer.modules().forEach(m->Class.forName(m,s));
                    Class<?> aClass = loadClass(s, Object.class);
                    if (hasComponentAnnotation(aClass) || RpgAddon.class.isAssignableFrom(aClass)) {
                        classesToLoad.add(aClass);
                    }
                } catch (Throwable ignored) {

                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Class<T> loadClass(String className, T as) {
        if (moduleLayer == null) {
            return null;
        }
        for (Module module : moduleLayer.modules()) {
            try {
                Class<T> t = (Class<T>) module.getClassLoader().loadClass(className);
                if (t != null) {
                    return t;
                }
            } catch (Throwable t) {
                t.printStackTrace(); //remove
            }
        }
        return null;
    }

    private static boolean skip(String s) {
        return exclusions.stream().anyMatch(s::startsWith);
    }

    private static boolean hasComponentAnnotation(Class aClass) {
        if (aClass == null) {
            return false;
        }
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (AddonScanner.annotations.contains(annotationType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isClassFile(JarEntry jarEntry) {
        if (!jarEntry.getName().endsWith(".class")) {
            return true;
        }
        if (jarEntry.getName().contains("META-INF")) {
            return true;
        }
        return false;
    }

    private static ModuleLayer addToClassPath(Path pluginsDir) {

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(pluginsDir, "*.jar")) {
            paths.forEach(p-> {
                try {
                    URL url = p.toUri().toURL();
                    addUrl.invoke(AddonScanner.class.getClassLoader(), url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);
        //List<String> plugins = pluginsFinder
        //        .findAll()
        //        .stream()
        //        .map(ModuleReference::descriptor)
        //        .map(ModuleDescriptor::name)
        //        .collect(Collectors.toList());
//
        //Configuration pluginsConfiguration = ModuleLayer
        //        .boot()
        //        .configuration()
        //        .resolve(pluginsFinder, ModuleFinder.of(), plugins);
//
        //return ModuleLayer
        //        .boot()
        //        .defineModulesWithOneLoader(pluginsConfiguration, AddonScanner.class.getClassLoader());
        return null;
    }

    private static String getValidClassName(JarEntry jarEntry) {
        if (isClassFile(jarEntry)) {
            return null;
        }
        if (jarEntry.getName().contains("module-info")) {
            return null;
        }
        String s = jarEntry.getName().replaceAll("/", ".");
        s = s.substring(0, s.length() - 6);

        return s;
    }

    private static class CannotAddToClassPath extends RuntimeException {
        private CannotAddToClassPath(String message, Throwable t) {
            super(message, t);
        }
    }

}
