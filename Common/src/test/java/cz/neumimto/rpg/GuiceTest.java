package cz.neumimto.rpg;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

/**
 * Created by NeumimTo on 16.9.2018.
 */
public class GuiceTest {

    @Test
    public void test() {
        Injector injector = Guice.createInjector();
        injector.getInstance(A.class).callBWhichCallsA();
        A instance = injector.getInstance(A.class);
        //is it com.sun.Proxy?
        System.out.println(instance.getClass().getCanonicalName());
        A instance1 = injector.getInstance(A.class);
        assert instance.b == instance1.b;

    }

}
