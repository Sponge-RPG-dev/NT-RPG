package cz.neumimto.rpg.api.effects.model.mappers;

import com.google.gson.Gson;
import cz.neumimto.rpg.api.effects.model.EffectModelMapper;
import cz.neumimto.rpg.api.utils.MathUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 6.1.2018.
 */
public abstract class SingleValueModelMapper extends EffectModelMapper {


    public SingleValueModelMapper(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        if (map.size() != 1) {
            throw new InvalidEffectModelDataFormat("SingleValueModelMapper but map.size != 1 " + new Gson().toJson(map));
        }
        String first = map.values().stream().findFirst().orElseThrow(() -> new InvalidEffectModelDataFormat("Its required at least one value in the map"));
        return parseStr(first);
    }

    public abstract Object parseStr(String s);


    public static class Int extends SingleValueModelMapper {

        public Int(Class<?> type) {
            super(type);
        }


        @Override
        public Object parseStr(String s) {
            return Integer.parseInt(MathUtils.extractNumber(s));
        }
    }

    public static class Double extends SingleValueModelMapper {

        public Double(Class<?> type) {
            super(type);
        }

        @Override
        public Object parseStr(String s) {
            return java.lang.Double.parseDouble(MathUtils.extractNumber(s));
        }
    }

    public static class Float extends SingleValueModelMapper {

        public Float(Class<?> type) {
            super(type);
        }

        @Override
        public Object parseStr(String s) {
            return java.lang.Float.parseFloat(MathUtils.extractNumber(s));
        }
    }

    public static class Long extends SingleValueModelMapper {

        public Long(Class<?> type) {
            super(type);
        }

        @Override
        public Object parseStr(String s) {
            return Integer.parseUnsignedInt(MathUtils.extractNumber(s));
        }
    }

    public static class Str extends SingleValueModelMapper {

        public Str(Class<?> type) {
            super(type);
        }

        @Override
        public Object parseStr(String s) {
            return s;
        }
    }


    public static class Void extends EffectModelMapper {

        public Void() {
            super(Void.class);
        }

        @Override
        public Object parse(Map<String, String> map) {
            return null;
        }
    }

    public static class InvalidEffectModelDataFormat extends RuntimeException {
        private InvalidEffectModelDataFormat(String message) {
            super(message);
        }
    }

}
