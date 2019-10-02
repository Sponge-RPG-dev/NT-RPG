package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;

import java.util.Set;

public class ClassPermissionAdapter implements Converter<Set<PlayerClassPermission>, Config> {

    @Override
    public Set<PlayerClassPermission> convertToField(Config value) {
        return null;
    }

    @Override
    public Config convertFromField(Set<PlayerClassPermission> value) {
        return null;
    }
}
