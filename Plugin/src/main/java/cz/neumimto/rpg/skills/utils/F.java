package cz.neumimto.rpg.skills.utils;

/**
 * Created by NeumimTo on 12.8.2018.
 */
public interface F {

	@FunctionalInterface
	interface QuadConsumer<T, U, V, W> {

		void accept(T t, U u, V v, W w);
	}

	@FunctionalInterface
	interface TriFunction<T, U, V, R> {

		R apply(T t, U u, V v);
	}

	@FunctionalInterface
	interface QuadFunction<T, U, V, W, X> {

		X accept(T t, U u, V v, W w);
	}
}
