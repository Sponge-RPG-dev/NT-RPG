

package cz.neumimto.rpg.common.utils;

/**
 * Created by NeumimTo on 12.3.2015.
 */
public class Pair<K, V> {

    public K key;
    public V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair() {
    }
}
