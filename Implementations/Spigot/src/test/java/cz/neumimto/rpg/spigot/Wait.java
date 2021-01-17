package cz.neumimto.rpg.spigot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Wait {

    public static void mainThread(int millis) {
        CountDownLatch lock = new CountDownLatch(1);
        try {
            lock.await(millis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
