package cz.neumimto.skills;

/**
 * Created by NeumimTo on 16.2.2015.
 */
public interface ISkillConfigParser<T> {
    T parse(String s);

    String stringify(T t);

    T get();
}
