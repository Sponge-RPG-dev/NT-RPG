package cz.neumimto.rpg;

import cz.neumimto.rpg.api.IResourceLoader;

import javax.inject.Singleton;
import java.net.URLClassLoader;
import java.util.Map;

@Singleton
public class TestResourceLoader implements IResourceLoader {
    @Override
    public Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public URLClassLoader getConfigClassLoader() {
        return null;
    }

    @Override
    public Map<String, URLClassLoader> getClassLoaderMap() {
        return null;
    }
}
