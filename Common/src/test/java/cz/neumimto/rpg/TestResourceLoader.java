package cz.neumimto.rpg;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;

import javax.inject.Singleton;
import java.io.File;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Singleton
public class TestResourceLoader implements IResourceLoader {
    @Override
    public void init() {

    }

    @Override
    public void loadJarFile(File f, boolean main) {

    }

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
        return Collections.emptyMap();
    }

    @Override
    public void reloadLocalizations(Locale locale) {

    }

    @Override
    public void loadExternalJars() {

    }

    @Override
    public void initializeComponents() {

    }
}
