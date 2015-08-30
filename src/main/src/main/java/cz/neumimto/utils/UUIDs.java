package cz.neumimto.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public class UUIDs {

    /*
    * UUID.randomUUID uses secure random, which is slow and we arent
    * using it for crypthography
    * */
    public static UUID random() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        return new UUID(current.nextLong(), current.nextLong());
    }
}
