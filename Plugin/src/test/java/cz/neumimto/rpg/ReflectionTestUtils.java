package cz.neumimto.rpg;

import cz.neumimto.rpg.api.logging.Log;

import java.lang.reflect.Field;

public class ReflectionTestUtils {

    public static void set(Object source, String fieldName, Object newValue) {
        try {
            Field declaredField = source.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(source, newValue);
        } catch (Exception e) {
            Log.error("Cannot modify Field " + source.getClass().getSimpleName() + "[" + fieldName + "]",e);
        }

    }
}
