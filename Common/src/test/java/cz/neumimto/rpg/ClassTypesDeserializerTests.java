package cz.neumimto.rpg;

import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.common.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.common.configuration.ClassTypesDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class ClassTypesDeserializerTests {

    @Test
    public void test_class_types_load() {
        URL resource = ClassTypesDeserializerTests.class.getClassLoader().getResource("classtypes.conf");
        try (FileConfig config = FileConfig.of(new File(resource.getFile()))) {
            config.load();
            ClassTypesDeserializer classTypesDeserializer = new ClassTypesDeserializer();

            Map<String, ClassTypeDefinition> map = classTypesDeserializer.convertToField(config);
            Assertions.assertEquals(2, map.size());

            ClassTypeDefinition definition = map.get("Primary");
            Assertions.assertEquals("Test", definition.getDefaultClass());

            Assertions.assertEquals("Color", definition.getPrimaryColor());
            Assertions.assertEquals("Color", definition.getSecondaryColor());
            Assertions.assertEquals("Color", definition.getDyeColor());
            Assertions.assertFalse(definition.isChangeable());
            Assertions.assertEquals(1, definition.getOrder());

            definition = map.get("Secondary");
            Assertions.assertNull(definition.getDefaultClass());
        }
    }
}
