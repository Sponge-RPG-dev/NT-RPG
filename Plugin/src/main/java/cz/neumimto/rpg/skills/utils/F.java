package cz.neumimto.rpg.skills.utils;

/**
 * Created by NeumimTo on 12.8.2018.
 */
public interface F {

    interface QuadConsumer<T,U,V,W> {
        void accept(T t, U u, V v, W w);
    }

    interface TriFunction<T, U, V> {
        V apply(T t, U u);
    }
}
