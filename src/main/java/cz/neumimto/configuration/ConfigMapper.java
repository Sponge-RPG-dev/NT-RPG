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

package cz.neumimto.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigMapper {
    private static class CMPair {
        protected Method parse;
        protected Method set;

        private static CMPair create(Method m1, Method m2) {
            CMPair cmPair = new CMPair();
            cmPair.parse = m1;
            cmPair.set = m2;
            return cmPair;
        }
    }

    private static final String LSEPARATOR;
    private static Map<String, ConfigMapper> currents = new ConcurrentHashMap<String, ConfigMapper>();
    public static Map<Class<?>, CMPair> primitiveWrappers = new HashMap<Class<?>, CMPair>() {{
        try {
            Method m = Field.class.getDeclaredMethod("set", Object.class, Object.class);
            put(Byte.class, CMPair.create(Byte.class.getDeclaredMethod("valueOf", String.class), m));
            put(Boolean.class, CMPair.create(Boolean.class.getDeclaredMethod("valueOf", String.class), m));
            put(Integer.class, CMPair.create(Integer.class.getDeclaredMethod("parseInt", String.class), m));
            put(Short.class, CMPair.create(Short.class.getDeclaredMethod("parseShort", String.class), m));
            put(Double.class, CMPair.create(Double.class.getDeclaredMethod("parseDouble", String.class), m));
            put(Float.class, CMPair.create(Float.class.getDeclaredMethod("parseFloat", String.class), m));
            put(Long.class, CMPair.create(Long.class.getDeclaredMethod("parseLong", String.class), m));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }};

    public static Map<Class<?>, CMPair> primitives = new HashMap<Class<?>, CMPair>() {{
        try {
            put(byte.class, CMPair.create(Boolean.class.getDeclaredMethod("valueOf", String.class), Field.class.getDeclaredMethod("setBoolean", Object.class, boolean.class)));
            put(int.class, CMPair.create(Integer.class.getDeclaredMethod("parseInt", String.class), Field.class.getDeclaredMethod("setInt", Object.class, int.class)));
            put(long.class, CMPair.create(Long.class.getDeclaredMethod("parseLong", String.class), Field.class.getDeclaredMethod("setLong", Object.class, long.class)));
            put(float.class, CMPair.create(Float.class.getDeclaredMethod("valueOf", String.class), Field.class.getDeclaredMethod("setFloat", Object.class, float.class)));
            put(short.class, CMPair.create(Short.class.getDeclaredMethod("parseShort", String.class), Field.class.getDeclaredMethod("setShort", Object.class, short.class)));
            put(double.class, CMPair.create(Double.class.getDeclaredMethod("parseDouble", String.class), Field.class.getDeclaredMethod("setDouble", Object.class, double.class)));
            put(boolean.class, CMPair.create(Boolean.class.getDeclaredMethod("parseBoolean", String.class), Field.class.getDeclaredMethod("setBoolean", Object.class, boolean.class)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }};

    static {
        LSEPARATOR = System.getProperty("line.separator");
    }

    public Path path;

    private ConfigMapper(String str, Path path) {
        this.path = path;
        currents.put(str.toLowerCase(), this);
    }

    public static void init(String id, Path workingFolderPath) {
        new ConfigMapper(id, workingFolderPath);
    }

    public static void init(String id, ProtectionDomain protectionDomain) {
        try {
            Path path = new File(protectionDomain.getCodeSource().getLocation().toURI().getPath()).toPath();
            init(id, path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static ConfigMapper get(String id) {
        return currents.get(id.toLowerCase());
    }

    /**
     * This method creates defaults and loads configuration from file.
     * All fields you wish to load must be static and annotated with @ConfigValue
     */
    public void loadClass(Class<?> clazz) {
        ConfigurationContainer container = clazz.getAnnotation(ConfigurationContainer.class);
        if (container == null)
            return;
        String filename = container.filename();
        if (filename.trim().equalsIgnoreCase("")) {
            filename = clazz.getSimpleName();
        }
        File file = null;
        if (container.path().trim().equalsIgnoreCase("")) {
            try {
                file = new File(new File(ConfigMapper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()), filename);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            file = new File(container.path().replace("{WorkingDir}", path.toString()), filename);
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileWriter writer = new FileWriter(file);
                Comment comment = clazz.getAnnotation(Comment.class);
                if (comment != null) {
                    writeComments(comment, writer);
                }
                for (Field f : clazz.getDeclaredFields()) {
                    ConfigValue value = f.getAnnotation(ConfigValue.class);
                    if (value == null) {
                        continue;
                    }
                    comment = f.getAnnotation(Comment.class);
                    if (comment != null) {
                        writeComments(comment, writer);
                    }
                    String valueid = value.name();
                    if (valueid.trim().equalsIgnoreCase("")) {
                        valueid = f.getName();
                    }
                    String content = " ";
                    if (f.getType().isPrimitive() || primitiveWrappers.containsKey(f.getType())) {
                        content = primitiveToString(f);
                    } else if (f.getType().isAssignableFrom(String.class)) {
                        content = "\"" + primitiveToString(f) + "\"";
                    } else if (Collection.class.isAssignableFrom(f.getType())) {
                        content = collectionToString(f);
                    } else if (Map.class.isAssignableFrom(f.getType())) {
                        content = mapToString(f);
                    }
                    writer.write(getSerializedNode(valueid) + " : " + content + LSEPARATOR);
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        com.typesafe.config.Config config = ConfigFactory.parseFile(file);
        try {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getType().isAssignableFrom(String.class)) {
                    f.set(null, config.getString(getNodeName(f)));
                } else {
                    CMPair cm = getCMPair(f);
                    if (cm != null) {
                        String s = config.getString(getNodeName(f));
                        Object value = cm.parse.invoke(null, s);
                        Method m = cm.set;
                        m.invoke(f, f, value);
                    } else if (f.getType().isAssignableFrom(List.class)) {
                        f.set(null, stringToList(f, config.getStringList(getNodeName(f)), config));
                    } else if (f.getType().isAssignableFrom(Set.class)) {
                        f.set(null, stringToSet(f, config.getStringList(getNodeName(f)), config));
                    } else if (f.getType().isAssignableFrom(Map.class)) {
                        f.set(null, stringToMap(f, config.getConfig(getNodeName(f))));
                    } else {
                        IMarshaller<?> marshaller = f.getAnnotation(ConfigValue.class).as().newInstance();
                        Object o = marshaller.unmarshall(config.getConfig(getNodeName(f)));
                        f.set(null, o);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private CMPair getCMPair(Field f) {
        if (f.getType().isPrimitive())
            return primitives.get(f.getType());
        return primitiveWrappers.get(f.getType());
    }

    private boolean isValidMap(Class<?> key, Class<?> value) {
        if (isWrappedPrimitiveOrString(key) && isWrappedPrimitiveOrString(value)) {
            return true;
        }
        return false;
    }

    private Map<?, ?> stringToMap(Field f, Config config) {
        try {
            Map<Object, Object> map = (Map<Object, Object>) f.get(null);
            map.clear();
            ParameterizedType type = (ParameterizedType) f.getGenericType();
            Class<?> key = (Class<?>) type.getActualTypeArguments()[0];
            Class<?> value = (Class<?>) type.getActualTypeArguments()[1];
            if (isValidMap(key, value)) {
                for (Map.Entry<String, com.typesafe.config.ConfigValue> val : config.entrySet()) {
                    Object k = new Object();
                    Object v = new Object();
                    if (key.isAssignableFrom(String.class)) {
                        k = val.getKey();
                    } else {
                        CMPair cm = primitiveWrappers.get(key);
                        k = cm.parse.invoke(null, val.getKey());
                    }
                    if (value.isAssignableFrom(String.class)) {
                        v = val.getValue().render();
                    } else {
                        CMPair cm = primitiveWrappers.get(value);
                        String input = val.getValue().render();
                        v = cm.parse.invoke(null, input);
                    }
                    map.put(k, v);
                }
            } else {
                IMapMarshaller<?, ?> mapMarshaller = (IMapMarshaller<?, ?>) f.getAnnotation(ConfigValue.class).as().newInstance();
                Map.Entry entry = mapMarshaller.unmarshall(config);
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getNodeName(Field field) {
        ConfigValue c = field.getAnnotation(ConfigValue.class);
        if (c != null) {
            if (!c.name().equalsIgnoreCase(""))
                return c.name();
        }
        return field.getName();
    }

    private Set<?> stringToSet(Field f, List<String> config, Config c) {
        try {
            Set set = (Set<?>) f.get(null);
            set.clear();
            set.addAll(stringToCollection(f, config, c));
            return set;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new HashSet<Object>();
    }

    private <T extends Number> List<T> strToWrapper(Class<T> excepted, List<String> list) {
        List l = new Vector<>();
        CMPair cmPair = primitiveWrappers.get(excepted);
        for (String s : list)
            try {
                l.add(cmPair.parse.invoke(null, s));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        return l;
    }

    private Collection<?> stringToCollection(Field f, List<String> config, Config c) {
        try {
            Collection list = (Collection) f.get(null);
            list.clear();
            Class<?> type = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
            if (type.isAssignableFrom(String.class)) {
                list = config;
            } else if (type.isAssignableFrom(Double.class)) {
                list = strToWrapper(Double.class, config);
            } else if (type.isAssignableFrom(Integer.class)) {
                list = strToWrapper(Integer.class, config);
            } else if (type.isAssignableFrom(Float.class)) {
                list = strToWrapper(Float.class, config);
            } else if (type.isAssignableFrom(Short.class)) {
                list = strToWrapper(Short.class, config);
            } else {
                IMarshaller<?> marshaller = f.getAnnotation(ConfigValue.class).as().newInstance();
                list.add(marshaller.unmarshall(c.getConfig(getNodeName(f))));
            }
            return list;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<?> stringToList(Field f, List<String> config, Config c) {
        try {
            List list = (List<?>) f.get(null);
            list.clear();
            list.addAll(stringToCollection(f, config, c));
            return list;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<Object>();
    }

    private String collectionToString(Field f) {
        String b = "[ ";
        Class<?> fclass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
        if (List.class.isAssignableFrom(f.getType()) || Set.class.isAssignableFrom(f.getType())) {
            try {
                if (fclass.isAssignableFrom(String.class)) {
                    for (Object o : (Collection) f.get(null)) {
                        b += "\"" + o + "\", ";
                    }
                } else if (isWrappedPrimitiveOrString(fclass)) {
                    for (Object o : (Collection) f.get(null)) {
                        b += o.toString() + ", ";
                    }
                } else {
                    ConfigValue v = f.getAnnotation(ConfigValue.class);
                    try {
                        IMarshaller m = v.as().newInstance();
                        for (Object o : (Collection) f.get(null)) {
                            b += "\"" + m.marshall(o) + "\", ";
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        b = b.substring(0, b.length() - 2);

        b += " ]";
        return b;
    }

    private String mapToString(Field f) {
        Map<?, ?> map = null;
        try {
            map = (Map) f.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String a = "{" + LSEPARATOR;
        ParameterizedType type = (ParameterizedType) f.getGenericType();
        Class<?> key = (Class<?>) type.getActualTypeArguments()[0];
        Class<?> value = (Class<?>) type.getActualTypeArguments()[1];
        if (isWrappedPrimitiveOrString(key) && isWrappedPrimitiveOrString(value)) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                a += "\t" + mapEntryToString(entry.getKey()) + " : " + mapEntryToString(entry.getValue()) + "," + LSEPARATOR;
            }
        } else {
            try {
                Class<IMapMarshaller> m = (Class<IMapMarshaller>) f.getAnnotation(ConfigValue.class).as();
                IMarshaller mar = m.newInstance();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    a += mar.marshall(entry) + "," + LSEPARATOR;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        a = a.substring(0, a.length() - (1 + LSEPARATOR.length()));
        return a + LSEPARATOR + "}";
    }


    private boolean isWrappedPrimitiveOrString(Class<?> clazz) {
        if (primitiveWrappers.containsKey(clazz)) {
            return true;
        }
        if (primitives.containsKey(clazz))
            return true;
        if (clazz.isAssignableFrom(String.class)) {
            return true;
        }
        return false;
    }

    private String primitiveToString(Field f) {
        try {
            return f.get(null) + "";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void writeComments(Comment comment, FileWriter writer) {
        for (String string : comment.content()) {
            try {
                writer.write("#" + string + LSEPARATOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String mapEntryToString(Object entry) {
        if (entry.getClass().isAssignableFrom(String.class))
            return "\"" + entry + "\"";
        if (primitiveWrappers.containsKey(entry.getClass())) {
            return entry.toString();
        }
        return null;
    }

    private String getSerializedNode(String nodeValue) {
        if (nodeValue.contains(".")) {
            return nodeValue;
        }

        return "\"" + nodeValue + "\"";
    }
}