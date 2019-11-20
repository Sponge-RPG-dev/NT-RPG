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

package cz.neumimto.rpg.api.utils;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.hocon.HoconWriter;
import cz.neumimto.rpg.api.RpgApi;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class FileUtils {

    public static URL getPluginUrl() {
        URL clsUrl = RpgApi.class.getResource(RpgApi.class.getSimpleName() + ".class");
        if (clsUrl != null) {
            try {
                URLConnection conn = clsUrl.openConnection();
                if (conn instanceof JarURLConnection) {
                    JarURLConnection connection = (JarURLConnection) conn;
                    return connection.getJarFileURL();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
}



