package cz.neumimto.rpg.common.reloading;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ReloadService {

	public static final String PLUGIN_CONFIG = "PC";

	private Map<String, List<Object>> reload = new HashMap<>();

	public void register(Object cl, String ctx) {
		if (reload.get(ctx) == null) {
			reload.put(ctx, Arrays.asList(cl));
		} else {
			reload.get(ctx).add(cl);
		}
	}

	public void reload(String ctx) {
		if (reload.get(ctx) != null) {
			for (Object o : reload.get(ctx)) {
				for (Method m : o.getClass().getDeclaredMethods()) {
					if (m.getAnnotation(Reload.class) != null) {
						try {
							m.invoke(o);
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
