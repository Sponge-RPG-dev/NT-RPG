/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.ioc;


import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by NeumimTo on 29.6.2015.
 */
@Singleton
public class IoC {

    private Map<Class<?>, Object> referenceMap = new HashMap<>();
    private Map<Object, Set<Method>> postProcess = new HashMap<>();
    private static IoC ioc;

    public Logger logger;

    protected IoC() {

    }

    public static IoC get() {
        if (ioc == null) {
            ioc = new IoC();
        }
        return ioc;
    }

    public void registerDependency(Object object) {
        referenceMap.put(object.getClass(), object);
    }

    public <T> T build(Class<? extends T> cl) {
        if (referenceMap.containsKey(cl)) {
            return (T) referenceMap.get(cl);
        }
        T t = null;
        try {
            t = cl.newInstance();
            if (logger != null)
                logger.debug("Creating Object from " + cl.getName() + " for dependency injection");
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return get(cl, t);
    }

    public void registerInterfaceImplementation(Class cl, Object o) {
        referenceMap.put(cl, o);
    }

    public <T> T get(Class<? extends T> cl, T t) {
        if (referenceMap.containsKey(cl))
            return (T) referenceMap.get(cl);
        if (cl.isAnnotationPresent(Singleton.class)) {
            register(t);
        }
        injectFields(t, cl);
        findAnnotatedMethods(cl, t);
        return t;
    }

    private void findAnnotatedMethods(Class cl, Object o) {
        if (postProcess.containsKey(o))
            return;
        if (logger != null)
            logger.debug("Looking for methods annotated with @PostProcess in a class " + cl.getName());
        Set<Method> set = new HashSet<>();
        Class superClass = cl.getSuperclass();
        findAnnotatedMethods(cl, o, set);
        if (logger != null)
            logger.debug(set.size() + " methods found");
        postProcess.put(o, set);
        if (superClass != Object.class) {
            if (logger != null)
                logger.debug("   - Processing superclass " + superClass.getName());
            findAnnotatedMethods(cl, o);
        }
    }

    private void findAnnotatedMethods(Class cl, Object o, Set<Method> set) {
        for (Method method : cl.getMethods()) {
            if (method.isAnnotationPresent(PostProcess.class)) {
                set.add(method);
            }
        }
    }

    private void injectFields(Object o, Class cl) {
        if (logger != null)
            logger.debug("Looking for fields annotated with @Inject in a class" + cl.getName());
        for (Field f : cl.getDeclaredFields()) {
            if (f.isAnnotationPresent(Inject.class)) {
                f.setAccessible(true);
                Class fieldtype = f.getType();
                if (logger != null)
                    logger.debug("  - Found field " + f.getName() + ", type of " + fieldtype.getName());
                Object instance = build(fieldtype);
                try {
                    if (logger != null)
                        logger.debug(" - injecting field " + f.getName() + " in " + cl.getName());
                    f.set(o, instance);
                    if (logger != null)
                        logger.debug(" - ok");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Class superClass = cl.getSuperclass();
        if (superClass != Object.class) {
            if (logger != null)
                logger.debug("   - Processing superclass " + superClass.getName());
            injectFields(o, superClass);
        }
        if (logger != null)
            logger.debug("  - Finished " + cl.getName());
    }


    public void register(Object o) {
        if (!referenceMap.containsKey(o.getClass()))
            referenceMap.put(o.getClass(), o);
    }

    public void postProcess() {
        long i = postProcess.values().stream().filter(m -> m.size() >= 1).count();
        if (logger != null)
            logger.debug("Invoking postprocess methods found in" + i + "classes");
        i = 0;
        int j = 0;
        for (Map.Entry<Object, Set<Method>> entry : entriesSortedByValues(postProcess)) {
            Set<Method> set = entry.getValue();
            for (Method m : set) {
                m.setAccessible(true);
                try {
                    if (logger != null)
                        logger.info("Invoking method: " + entry.getKey().getClass().getSimpleName() + "." + m.getName() + " with priority " + m.getAnnotation(PostProcess.class).priority());
                    m.invoke(entry.getKey());
                    i++;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    j++;
                }
            }
        }
        if (logger != null)
            logger.debug(" - Invoked: " + i + " methods, Failed: " + j + " are they accessible and argumentless?");
        postProcess.clear();
    }

    static SortedSet<Map.Entry<Object, Set<Method>>> entriesSortedByValues(Map<Object, Set<Method>> map) {
        SortedSet<Map.Entry<Object, Set<Method>>> sortedEntries = new TreeSet<Map.Entry<Object, Set<Method>>>(
                (o1, o2) -> {
                    int res = 0;
                    int first = 0;
                    int second = 0;
                    for (Method method : o1.getValue()) {
                        first = method.getAnnotation(PostProcess.class).priority();
                        break;
                    }
                    for (Method method : o2.getValue()) {
                        second = method.getAnnotation(PostProcess.class).priority();
                        break;
                    }
                    res = first - second;
                    return res != 0 ? res : 1;
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
