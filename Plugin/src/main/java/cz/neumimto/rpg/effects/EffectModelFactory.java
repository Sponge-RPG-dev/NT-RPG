package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.Pair;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class EffectModelFactory {

    @SuppressWarnings("unchecked")
    public static <T> T createFrom(Class<T> model, List<Text> data) {
        LoreMapper loreMapper = loreMapperMap.get(model);
        if (loreMapper == null) {
            loreMapper = new LoreMapper(model);
            loreMapperMap.put(model, loreMapper);
        }

        try {
            return (T) loreMapper.parse(data);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T createFrom(Class<T> model, String csv) {
        return null;
    }

    public static void validateModelClass(Class<?> model) throws InvalidEffectModelException {

    }

    private static Map<Class<?>, LoreMapper> loreMapperMap = new HashMap<>();

    private class ModelPair {
        private LoreMapper loreBuilder;
    }

    public static class Mapper {
        private Class<?> type;
        Map<String,Pair<Class<?>, Field>> nameTypeCache = new HashMap<>();
        public Mapper(Class<?> type) {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                nameTypeCache.put(field.getName().toLowerCase(), new Pair<>(field.getType(), field));
            }
            try {
                type.getConstructor().setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            this.type = type;
        }
    }

    public static class LoreMapper extends Mapper {

        public LoreMapper(Class<?> t) {
            super(t);
        }

        //todo  asm-fy this later
        public Object parse(List<Text> data) throws IllegalAccessException, InstantiationException {
            Object model = super.type.newInstance();
            for (Text dataa : data) {
                String s = dataa.toPlain();
                String[] split = s.split(":");
                String name = Utils.extractClassMember(split[0]);
                Pair<Class<?>, Field> classFieldPair = nameTypeCache.get(name.toLowerCase());
                if (classFieldPair == null) continue;
                classFieldPair.value.set(model, typeMapperMap.get(classFieldPair.key).apply(Utils.extractNumber(split[1])));

            }
            return model;
        }
    }
    
    private static Map<Class<?>, Function<String, Object>> typeMapperMap = new HashMap<>();

    static {

        typeMapperMap.put(int.class, Integer::parseInt);
        typeMapperMap.put(Integer.class, Integer::parseInt);
        typeMapperMap.put(double.class, Double::parseDouble);
        typeMapperMap.put(Double.class, Double::parseDouble);
        typeMapperMap.put(float.class, Float::parseFloat);
        typeMapperMap.put(Float.class, Float::parseFloat);
        typeMapperMap.put(long.class, Long::parseLong);
        typeMapperMap.put(Long.class, Long::parseLong);
        typeMapperMap.put(String.class, s -> s);
    }
}
