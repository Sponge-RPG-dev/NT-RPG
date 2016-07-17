package cz.neumimto.dei;

import com.google.common.reflect.ClassPath;
import cz.neumimto.core.FindPersistenceContextEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by ja on 5.7.2016.
 */
@Plugin(id = "cz.neumimto.dei",
        version = "1.0.0",
        name = "NT-dei",
        dependencies = {@Dependency(id = "cz.neumimto.core")})
public class DEI {

    private Thread jobRunner;

    @Listener
    public void onFindPersistentContext(FindPersistenceContextEvent event) throws IOException {
        event.getClasses()
                .addAll(ClassPath.from(Thread.currentThread().getContextClassLoader())
                        .getTopLevelClassesRecursive("cz.neumimto.dei.entity.database")
                .stream()
                        .map(ClassPath.ClassInfo::load)
                        .filter(aClass -> aClass.isAnnotationPresent(Entity.class))
                        .collect(Collectors.toList()));
    }

    @Listener
    public void onGameStart(GameAboutToStartServerEvent event) throws Exception {
        ClassPath.from(Thread.currentThread().getContextClassLoader())
                .getTopLevelClassesRecursive("cz.neumimto.dei.listeners")
                .stream().map(ClassPath.ClassInfo::load)
                .filter(aClass -> aClass.isAnnotationPresent(ListenerClass.class))
                .forEach(a -> {
                    try {
                        Sponge.getGame().getEventManager().registerListeners(this, a.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        jobRunner = new Thread(this::startScheduler);
        jobRunner.start();


        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(Text.of("Hello World Command"))
                .permission("myplugin.command.helloworld")

                .executor(new HelloWorldCommand())
                .build();

        Sponge.getCommandManager().register(plugin, myCommandSpec, "helloworld", "hello", "test");
    }

    public void startScheduler() {

    }
}
