package cz.neumimto.rpg.common;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.logging.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonScanner {

    private final static String INNERCLASS_SEPARATOR = "$";

    private static boolean stage;

    private static Path addonDir;

    private static Set<Class<?>> annotations = new HashSet<>();
    private static Set<Class<?>> classesToLoad = new HashSet<>();
    private static Set<String> exclusions = new HashSet<>();

    static {
        annotations.add(IResourceLoader.Skill.class);
        annotations.add(IResourceLoader.Command.class);
        annotations.add(IResourceLoader.ModelMapper.class);
        annotations.add(IResourceLoader.ListenerClass.class);

        //Hibernate dep
        exclusions.add("javax");
    }

    public static void setDeployedDir(Path deployedDir) {
        AddonScanner.deployedDir = deployedDir;
        if (!Files.exists(deployedDir)) {
            try {
                Files.createDirectory(deployedDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static Path deployedDir;

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
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(classesToLoad);
        classesToLoad.clear();
        return classes;
    }

    public static void prepareAddons() {
        Map<Boolean, Set<Path>> map = discoverJarCandidates();
        copyReloadableJarModulesToDeployedDir(map);

        if (!stage) {
            findRelevantClassCandidatesinPath(map.get(false));
        }
        findRelevantClassCandidatesinPath(map.get(true));

    }

    private static void findRelevantClassCandidatesinPath(Set<Path> paths) {
        for (Path path : paths) {
            addToClassPath(path);
        }
        for (Path path : paths) {
            visitJarFile(path);
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
                    Class<?> aClass = Class.forName(s);
                    if (hasComponentAnnotation(aClass) || RpgAddon.class.isAssignableFrom(aClass)) {
                        classesToLoad.add(aClass);
                    }
                } catch (NoClassDefFoundError ignored) {
                    int i = 0;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean skip(String s) {
        return exclusions.stream().anyMatch(s::startsWith);
    }

    private static boolean hasComponentAnnotation(Class aClass) {
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType  = annotation.annotationType();
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

    private static void addToClassPath(Path path) {
        try {
            File file = path.toFile();
            Log.info("Adding " + file.getParentFile().getName() + "/" + file.getName() + " to classpath");
            addPath(file.toPath().toUri().toURL());
        } catch (Exception e) {
            throw new CannotAddToClassPath(e.getMessage());
        }
    }

    private static void addPath(URL url) throws Exception {
        URLClassLoader urlClassLoader = (URLClassLoader) AddonScanner.class.getClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{url});
    }

    private static void copyReloadableJarModulesToDeployedDir(Map<Boolean, Set<Path>> map) {
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(deployedDir, "*.jar");
            paths.forEach(p -> p.toFile().delete());
            for (Path path : map.get(true)) {
                Files.copy(path, deployedDir.resolve(path.getFileName()));
            }
        } catch (IOException e) {
            throw new CannotReadOrWriteFS(e.getMessage());
        }
    }

    private static Map<Boolean, Set<Path>> discoverJarCandidates() {
        Map<Boolean, Set<Path>> map = new HashMap<>();
        map.put(true, new HashSet<>());
        map.put(false, new HashSet<>());
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(addonDir, "*.jar")) {
            for (Path path : paths) {
                JarFile jarFile = new JarFile(path.toFile());
                map.get(isModuleUnreloadable(jarFile)).add(path);
            }
        } catch (IOException e) {
            throw new CannotReadOrWriteFS(e.getMessage());
        }
        return map;
    }

    private static boolean isModuleUnreloadable(JarFile jarFile) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().startsWith("META-INF/rpg-addon")) {
                return true;
            }
        }
        return false;
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

    private static class CannotReadOrWriteFS extends RuntimeException {
        private CannotReadOrWriteFS(String message) {
            super(message);
        }
    }

    private static class CannotAddToClassPath extends RuntimeException {
        private CannotAddToClassPath(String message) {
            super(message);
        }
    }


}
