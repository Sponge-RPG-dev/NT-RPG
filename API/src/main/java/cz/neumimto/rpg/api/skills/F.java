package cz.neumimto.rpg.api.skills;

/**
 * Created by NeumimTo on 12.8.2018.
 */
public interface F {

    @FunctionalInterface
    interface QuadConsumer<T, U, V, W> {

        void accept(T t, U u, V v, W w);
    }

    @FunctionalInterface
    interface PentaConsumer<T, U, V, W, X> {

        void accept(T t, U u, V v, W w, X x);
    }

    @FunctionalInterface
    interface TriFunction<T, U, V, R> {

        R apply(T t, U u, V v);
    }

    @FunctionalInterface
    interface QuadFunction<T, U, V, W, X> {

        X accept(T t, U u, V v, W w);
    }

    @FunctionalInterface
    interface PentaFunction<S, T, U, V, W, X> {

        X accept(S s, T t, U u, V v, W w);
    }

}
