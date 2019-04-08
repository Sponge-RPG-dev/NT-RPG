package cz.neumimto.rpg.common.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ItemStringTest {

    @ParameterizedTest
    @MethodSource("provideItemStrings")
    void parse(String input, ItemString expected) {
        ItemString parsed = ItemString.parse(input);

        Assertions.assertEquals(parsed.armor, expected.armor);
        Assertions.assertEquals(parsed.model, expected.model);
        Assertions.assertEquals(parsed.itemId, expected.itemId);
        Assertions.assertEquals(parsed.damage, expected.damage);
    }

    private static Stream<Arguments> provideItemStrings() {
        return Stream.of(
                Arguments.of("item:id", new ItemString("item:id",0,0,null)),
                Arguments.of("item:id;10", new ItemString("item:id",10,0,null)),
                Arguments.of("item:id;10.8", new ItemString("item:id",10.8D,0,null)),
                Arguments.of("item:id;model;10", new ItemString("item:id",10,0,"model")),
                Arguments.of("item:id;10;model", new ItemString("item:id",10,0,"model")),
                Arguments.of("item:id;model", new ItemString("item:id",0,0,"model"))
        );
    }
}