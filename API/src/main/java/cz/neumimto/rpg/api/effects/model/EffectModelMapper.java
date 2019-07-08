package cz.neumimto.rpg.api.effects.model;


import cz.neumimto.rpg.api.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by NeumimTo on 6.1.2018.
 */
public abstract class EffectModelMapper {

    protected final static Map<Class<?>, Function<String, Object>> typeMapperMap = new HashMap<>();

    static {


        typeMapperMap.put(int.class, s -> {
            if (s == null) {
                return 0;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return 0;
            }
            return Integer.parseInt(s);
        });
        typeMapperMap.put(Integer.class, s -> {
            if (s == null) {
                return null;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return null;
            }
            return Integer.parseInt(s);
        });
        typeMapperMap.put(double.class, s -> {
            if (s == null) {
                return 0D;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return 0D;
            }
            return Double.parseDouble(s);
        });
        typeMapperMap.put(Double.class, s -> {
            if (s == null) {
                return null;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return null;
            }
            return Double.parseDouble(s);
        });
        typeMapperMap.put(float.class, s -> {
            if (s == null) {
                return 0f;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return 0f;
            }
            return Float.parseFloat(s);
        });
        typeMapperMap.put(Float.class, s -> {
            if (s == null) {
                return null;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return null;
            }
            return Float.parseFloat(s);
        });
        typeMapperMap.put(long.class, s -> {
            if (s == null) {
                return 0L;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return 0L;
            }
            return Long.parseLong(s);
        });
        typeMapperMap.put(Long.class, s -> {
            if (s == null) {
                return null;
            }
            s = MathUtils.extractNumber(s);
            if (s == null) {
                return null;
            }
            return Long.parseLong(s);
        });
        typeMapperMap.put(String.class, s -> s);
    }

    private final Class<?> type;

    public EffectModelMapper(Class<?> type) {
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

    public double parseDouble(Map<String, String> map, String key) {
        String s = map.get(key);
        return s == null ? 0D : Double.parseDouble(MathUtils.extractNumber(s));
    }

    public int parseInt(Map<String, String> map, String key) {
        String s = map.get(key);
        return s == null ? 0 : Integer.parseInt(MathUtils.extractNumber(s));
    }

    public float parseFloat(Map<String, String> map, String key) {
        String s = map.get(key);
        return s == null ? 0f : Float.parseFloat(MathUtils.extractNumber(s));
    }

    public long parseLong(Map<String, String> map, String key) {
        String s = map.get(key);
        return s == null ? 0L : Long.parseLong(MathUtils.extractNumber(s));
    }


    public boolean parseBoolean(Map<String, String> map, String key) {
        String s = map.get(key);
        return s != null && Boolean.parseBoolean(s);
    }

}

