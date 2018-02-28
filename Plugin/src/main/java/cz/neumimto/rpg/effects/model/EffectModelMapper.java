package cz.neumimto.rpg.effects.model;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by NeumimTo on 6.1.2018.
 */
public abstract class EffectModelMapper {
    public static Map<Class<?>, Function<String, Object>> typeMapperMap = new HashMap<>();

    static {


        typeMapperMap.put(int.class, s -> {
            if (s == null)
                return 0;
            s = Utils.extractNumber(s);
            if (s == null)
                return 0;
            return Integer.parseInt(s);
        });
        typeMapperMap.put(Integer.class, s -> {
            if (s == null)
                return null;
            s = Utils.extractNumber(s);
            if (s == null)
                return null;
            return Integer.parseInt(s);
        });
        typeMapperMap.put(double.class, s -> {
            if (s == null)
                return 0D;
            s = Utils.extractNumber(s);
            if (s == null)
                return 0D;
            return Double.parseDouble(s);
        });
        typeMapperMap.put(Double.class, s -> {
            if (s == null)
                return null;
            s = Utils.extractNumber(s);
            if (s == null)
                return null;
            return Double.parseDouble(s);
        });
        typeMapperMap.put(float.class, s -> {
            if (s == null)
                return 0f;
            s = Utils.extractNumber(s);
            if (s == null)
                return 0f;
            return Float.parseFloat(s);
        });
        typeMapperMap.put(Float.class, s -> {
            if (s == null)
                return null;
            s = Utils.extractNumber(s);
            if (s == null)
                return null;
            return Float.parseFloat(s);
        });
        typeMapperMap.put(long.class, s -> {
            if (s == null)
                return 0L;
            s = Utils.extractNumber(s);
            if (s == null)
                return 0L;
            return Long.parseLong(s);
        });
        typeMapperMap.put(Long.class, s -> {
            if (s == null)
                return null;
            s = Utils.extractNumber(s);
            if (s == null)
                return null;
            return Long.parseLong(s);
        });
        typeMapperMap.put(String.class, s -> s);
    }

    private final Class<?> type;
    Map<String,Pair<Class<?>, Field>> nameTypeCache = new HashMap<>();

    public EffectModelMapper(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            nameTypeCache.put(field.getName().toLowerCase(), new Pair<>(field.getType(), field));
        }

        this.type = type;
    }

    protected Map<Class<?>, Function<String, Object>> getCache() {
        return typeMapperMap;
    }

    public Class<?> getType() {
        return type;
    }

    protected String getTypeName() {
        return type.getSimpleName();
    }

    public abstract Object parse(Map<String, String> map);
}

