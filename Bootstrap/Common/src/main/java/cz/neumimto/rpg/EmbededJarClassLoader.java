package cz.neumimto.rpg;

import java.net.URL;
import java.net.URLClassLoader;

public class EmbededJarClassLoader extends URLClassLoader {
    public EmbededJarClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
