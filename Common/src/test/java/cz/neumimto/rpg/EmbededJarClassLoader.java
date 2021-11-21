package cz.neumimto.rpg;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class EmbededJarClassLoader extends URLClassLoader {
    private Map<String, Class> classMap = new HashMap<>();

    public EmbededJarClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (classMap.containsKey(name)) {
            return classMap.get(name);
        }
        return super.loadClass(name);
    }

    public void addClass(Class c) {
        classMap.put(c.getName(), c);
    }
}
