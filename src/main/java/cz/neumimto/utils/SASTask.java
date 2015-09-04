package cz.neumimto.utils;


import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.TaskBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by NeumimTo on 17.7.2015.
 */
public class SASTask<T, U> {
    Function<T, U> function;
    Consumer<U> consumer;

    public SASTask async(Function<T, U> funct) {
        this.function = funct;
        return this;
    }

    public SASTask sync(Consumer<U> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void start(T t, Game game, Object plugin) {
        TaskBuilder taskBuilder = game.getScheduler().createTaskBuilder();
        taskBuilder.async().execute(() -> {
            U u1 = function.apply(t);
            game.getScheduler().createTaskBuilder().execute(() -> consumer.accept(u1)).submit(plugin);
        }).submit(plugin);

    }

}
