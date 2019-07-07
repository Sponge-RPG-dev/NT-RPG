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

package cz.neumimto.rpg.common.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class ResourceClassLoader extends URLClassLoader {


    private final String name;

    public ResourceClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.name = name;
    }

    public ResourceClassLoader(String name, URL[] urls) {
        super(urls);
        this.name = name;
    }

    public ResourceClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
