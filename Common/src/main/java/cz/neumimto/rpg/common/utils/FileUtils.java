

package cz.neumimto.rpg.common.utils;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.hocon.HoconWriter;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class FileUtils {

    public static File getPluginFile(Class c) {
        URL clsUrl = c.getResource(c.getSimpleName() + ".class");
        if (clsUrl != null) {
            try {
                URLConnection conn = clsUrl.openConnection();
                if (conn instanceof JarURLConnection) {
                    JarURLConnection connection = (JarURLConnection) conn;
                    return new File(connection.getJarFileURL().toURI().getSchemeSpecificPart());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void createFileIfNotExists(Path path) {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Path createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }


    public static void generateConfigFile(Object data, File file) {
        CommentedConfig c = CommentedConfig.inMemory();
        new ObjectConverter().toConfig(data, c);
        HoconWriter hoconWriter = new HoconWriter();
        hoconWriter.write(c, file, WritingMode.REPLACE);
    }

    public static void deleteDirectory(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }

    public static void copyDirectory(File from, File to, Function<Path, Boolean> copyFn) {
        Path source =  from.toPath();
        Path target = to.toPath();
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(target.resolve(source.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (copyFn.apply(file)) {
                        Files.copy(file, target.resolve(source.relativize(file)));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



