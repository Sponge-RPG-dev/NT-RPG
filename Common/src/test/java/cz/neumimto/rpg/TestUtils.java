package cz.neumimto.rpg;

import java.lang.reflect.Field;

/**
 * Created by fs on 7.10.15.
 */
public class TestUtils {

    static void setField(Object o, String f, Object v) {
        try {
            Field declaredField = o.getClass().getDeclaredField(f);
            declaredField.setAccessible(true);

            declaredField.set(o, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
