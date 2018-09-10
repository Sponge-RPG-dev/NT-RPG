package cz.neumimto.rpg.effects;

public interface StringParsable<T> {

    String toString();

    T fromString(String string);
}
