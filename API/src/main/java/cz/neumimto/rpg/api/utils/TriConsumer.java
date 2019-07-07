package cz.neumimto.rpg.api.utils;

/**
 * Created by NeumimTo on 29.4.2018.
 */
@FunctionalInterface
public interface TriConsumer<K, V, S> {

    void accept(K k, V v, S s);

}
