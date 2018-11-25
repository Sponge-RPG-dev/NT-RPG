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

package cz.neumimto.rpg.utils;

import cz.neumimto.rpg.NtRpgPlugin;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.CodeSource;

/**
 * Created by NeumimTo on 31.1.2015.
 */
public class FileUtils {


	public static String getJarContainingFolder(Class<?> aclass) throws Exception {
		CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
		String str = codeSource.getLocation().toURI().toString().split("!")[0].substring(10);
		return str;
	}

	public static URL getPluginUrl() {
		URL clsUrl = NtRpgPlugin.class.getResource(NtRpgPlugin.class.getSimpleName() + ".class");
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
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(data);
			HoconConfigurationLoader hcl = HoconConfigurationLoader.builder()
					.setPath(file.toPath())
					.build();
			SimpleCommentedConfigurationNode scn = SimpleCommentedConfigurationNode.root();
			configMapper.serialize(scn);

			hcl.save(scn);
		} catch (Exception e) {
			throw new RuntimeException("Could not create file " + file, e);
		}
	}
}



