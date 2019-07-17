package cz.neumimto.rpg.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemDamageProcessor {

    @Test
    public void testSum() {
        double v = new Sum().get(10, 5);
        Assertions.assertSame(15, v);
    }

    @Test
    public void testMax() {
        double v = new Max().get(10, 5);
        Assertions.assertSame(10, v);
    }
}