package cz.neumimto;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by NeumimTo on 27.12.2014.
 */
//todo use JarClassLoader
public class ResourceClassLoader extends URLClassLoader {
    public ResourceClassLoader(URLClassLoader parent) {
        super(parent.getURLs(), parent);

    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
