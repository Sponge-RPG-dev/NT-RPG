package cz.neumimto.rpg;

import cz.neumimto.rpg.common.configuration.ItemString;
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

        Assertions.assertEquals(parsed.variant, expected.variant);
        Assertions.assertEquals(parsed.itemId, expected.itemId);
    }

    private static Stream<Arguments> provideItemStrings() {
        return Stream.of(
                Arguments.of("item:id", new ItemString("item:id", null, "")),
                Arguments.of("item:id;damage=10", new ItemString("item:id", null, "")),
                Arguments.of("item:id;damage=10.8", new ItemString("item:id", null, "")),
                Arguments.of("item:id;model=variant;damage=10", new ItemString("item:id", "variant", "")),
                Arguments.of("item:id;damage=10;model=variant", new ItemString("item:id", "variant", "")),
                Arguments.of("item:id;model=variant", new ItemString("item:id", "variant", ""))
        );
    }
}