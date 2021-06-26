package cz.neumimto.rpg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

class RpgModuleLayer {

    static NtRpgBootstrap getBootstrap(String embedJar, ClassLoader parent) throws Exception {

        URL resource = parent.getResource(embedJar);
        Path tempDirectory = Files.createTempDirectory("ntrpg-loader");
        tempDirectory.toFile().deleteOnExit();

        try (InputStream in = resource.openStream()) {
            Files.copy(in, tempDirectory.resolve("ntrpg.jar.temp"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy ntrpg jar to " + tempDirectory);
        }

        ModuleFinder pluginsFinder = ModuleFinder.of(tempDirectory);
        List<String> plugins = pluginsFinder
                .findAll()
                .stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .collect(Collectors.toList());

        Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);


        ModuleLayer layer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, new EmbededJarClassLoader(parent));


        ServiceLoader<NtRpgBootstrap> load = ServiceLoader.load(layer, NtRpgBootstrap.class);
        load.reload();
        return load.findFirst().get();
        //Class<NtRpgBootstrap> c = (Class<NtRpgBootstrap>) layer.findLoader("cz.neumimto.rog").loadClass("cz.neumimto.rpg.NtRpgBootstrap");
        //return c.getDeclaredConstructor().newInstance();


    }
}
