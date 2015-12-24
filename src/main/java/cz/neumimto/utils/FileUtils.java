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

    public static File getPluginJar() {
        try {
            String s = getJarContainingFolder(NtRpgPlugin.class);
            return new File(s);
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

}



