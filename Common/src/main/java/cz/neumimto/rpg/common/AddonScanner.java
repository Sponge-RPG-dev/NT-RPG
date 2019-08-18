package cz.neumimto.rpg.common;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.skills.PlayerSkillHandlers;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cz.neumimto.rpg.api.logging.Log.info;

public class AddonScanner {

    private static boolean stage;

    private static Path addonDir;

    private static Set<String> annotations = new HashSet<>();
    private static Set<String> classesToLoad = new HashSet<>();

    static {
        annotations.add(simpleName(IResourceLoader.Skill.class));
        annotations.add(simpleName(IResourceLoader.Command.class));
        annotations.add(simpleName(IResourceLoader.ModelMapper.class));
        annotations.add(simpleName(IResourceLoader.ListenerClass.class));
        annotations.add(simpleName(Singleton.class));
    }

    private static String simpleName(Class c) {
        return "L"+c.getCanonicalName().replaceAll("\\.", "\\\\") + ";";
    }

    public static void setDeployedDir(Path deployedDir) {
        AddonScanner.deployedDir = deployedDir;
    }

    private static Path deployedDir;

    public static void setAddonDir(Path addonDir) {
        AddonScanner.addonDir = addonDir;
    }

    public static void onlyReloads() {
        AddonScanner.stage = true;
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
                if (isClassFile(jarEntry)) {
                    continue;
                }
                try (InputStream is = jarFile.getInputStream(jarEntry)) {
                    ClassReader classReader = new ClassReader(is);
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, 0);


                    String className = classReader.getClassName();
                    if (classNode.visibleAnnotations != null) {
                        if (classNode.visibleAnnotations.stream().anyMatch(a -> annotations.contains(a.desc)))
                            classesToLoad.add(className);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private static boolean isClassFile(JarEntry jarEntry) {
        if (!jarEntry.getName().endsWith(".class")) {
            return true;
        }
        if (jarEntry.getName().startsWith("META-INF")) {
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
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{url});
    }

    private static void copyReloadableJarModulesToDeployedDir(Map<Boolean, Set<Path>> map) {
        try {
            if (!Files.exists(deployedDir)) {
                Files.createDirectory(deployedDir);
            }
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

            if (isClassFile(jarEntry)) {
                continue;
            }
            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                ClassReader classReader = new ClassReader(inputStream);
                String[] interfaces = classReader.getInterfaces();
                for (String anInterface : interfaces) {
                    if (anInterface.equalsIgnoreCase(RpgAddon.class.getCanonicalName())) {
                        info("Found a module - " + classReader.getClassName() + " in " + jarFile.getName());

                        return true;
                    }
                }
            } catch (IllegalArgumentException | IOException e) {
                continue;
            }


        }
        return false;
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
