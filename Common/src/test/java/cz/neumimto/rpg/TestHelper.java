package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.persistance.model.JPACharacterClass;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TestHelper {

    public static Unsafe getUnsafe() throws Exception {
        Field f =Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    public static ClassDefinition createClassDefinition() throws Exception {
        ClassDefinition classDefinition = new ClassDefinition("testclassdef", "Primary");
        return classDefinition;
    }

    public static ISkill createMockSkill(String skill) throws Exception {
        ISkill mock = Mockito.mock(ISkill.class);
        Mockito.when(mock.getId()).thenReturn(skill);
        return mock;
    }


    public static CharacterClass createCharacterClass() throws Exception {
        CharacterClass characterClass = new JPACharacterClass();
        characterClass.setId(1L);
        characterClass.setName("testClassDef");
        return characterClass;
    }

    public static void setField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Object getField(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }


    public static void setupLog() {
        Log.setLogger(LoggerFactory.getLogger("test"));
    }
}
