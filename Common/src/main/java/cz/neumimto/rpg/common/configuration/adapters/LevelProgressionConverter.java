package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import cz.neumimto.rpg.common.entity.players.leveling.EmptyLevelProgression;
import cz.neumimto.rpg.common.entity.players.leveling.ILevelProgression;

public class LevelProgressionConverter implements Converter<ILevelProgression, Config> {

    @Override
    public ILevelProgression convertToField(Config value) {
        if (value == null) {
            return new EmptyLevelProgression();
        }
        String class__ = value.get("__class__");
        try {
            Class<?> aClass = Class.forName(String.valueOf(class__));
            return (ILevelProgression) new ObjectConverter().toObject(value, () -> {
                try {
                    return aClass.newInstance();
                } catch (Exception e) {
                    throw new UnknownILevelProgressException(class__);
                }
            });
        } catch (ClassNotFoundException e) {
            throw new UnknownILevelProgressException(class__);
        }
    }

    @Override
    public Config convertFromField(ILevelProgression value) {
        //NOOP
        return Config.inMemory();
    }

    public static class UnknownILevelProgressException extends RuntimeException {

        public UnknownILevelProgressException(String class__) {
            super(class__);
        }

    }
}
