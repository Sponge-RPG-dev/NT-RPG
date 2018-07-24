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

package cz.neumimto.rpg;

import org.apache.commons.lang3.ArrayUtils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class ResourceClassLoader extends URLClassLoader {
	public ResourceClassLoader(URL additionalPool, URLClassLoader parent) {
		super(ArrayUtils.add(parent.getURLs(), additionalPool), parent);

	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}
