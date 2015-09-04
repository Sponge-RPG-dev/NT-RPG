package cz.neumimto.utils;

import cz.neumimto.NtRpgPlugin;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.jar.JarFile;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class FileUtils {


    public static String getJarContainingFolder(Class aclass) throws Exception {
        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
        File jarFile = null;
        String str = codeSource.getLocation().toURI().toString().split("!")[0].substring(10);
        return str;
    }

    public static JarFile getPluginJar() {
        try {
            String s = getJarContainingFolder(NtRpgPlugin.class);
            return new JarFile(s);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }

    public static void createFileIfNotExists(Path path) {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static Path createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return path;
    }

    public static void closeStream(Closeable closeable) {
    }
}



