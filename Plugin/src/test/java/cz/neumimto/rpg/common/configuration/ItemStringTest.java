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

        Assertions.assertSame(parsed.armor, expected.armor);
        Assertions.assertSame(parsed.model, expected.model);
        Assertions.assertSame(parsed.itemId, expected.itemId);
        Assertions.assertSame(parsed.damage, expected.damage);
    }

    private static Stream<Arguments> provideItemStrings() {
        return Stream.of(
                Arguments.of("item:id;10", new ItemString("item:id",10,0,null)),
                Arguments.of("item:id;10.8", new ItemString("item:id",10.8D,0,null)),
                Arguments.of("item:id;model;10", new ItemString("item:id",10,0,"model")),
                Arguments.of("item:id;model", new ItemString("item:id",0,0,"model"))
        );
    }
}