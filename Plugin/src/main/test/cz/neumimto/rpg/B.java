package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;

/**
 * Created by NeumimTo on 16.9.2018.
 */
public class B {

	@Inject A a;

	public void callA() {
		a.a();
	}

	public void b() {
		System.out.println("B.b()");
	}
}
