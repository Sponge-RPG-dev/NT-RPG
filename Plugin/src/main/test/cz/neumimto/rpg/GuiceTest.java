package cz.neumimto.rpg;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

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
		System.out.println(instance.b == injector.getInstance(B.class));
	}

}
