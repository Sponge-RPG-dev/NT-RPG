package cz.neumimto.rpg.spigot.resources;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.ResourceManagerImpl;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.spigot.services.IRpgListener;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpigotResourceManager extends ResourceManagerImpl {

    @Inject
    private Injector injector;

    @Override
    public void loadServices() {
        super.loadServices();
        load(IRpgListener.class, getClass().getClassLoader()).forEach(a -> {
            ListenerClass annotation = a.getClass().getAnnotation(ListenerClass.class);
            if (annotation != null
                    && !annotation.value().isBlank()
                    && !Rpg.get().getPluginConfig().FEATURES.contains(annotation.value())) {
                return;
            }
            injector.injectMembers(a);
            Rpg.get().registerListeners(a);
        });

    }
}
