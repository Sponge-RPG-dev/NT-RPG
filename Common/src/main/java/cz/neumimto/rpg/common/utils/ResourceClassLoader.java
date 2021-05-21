

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
