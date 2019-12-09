package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;

import java.util.*;

public class ClassPermissionAdapter implements Converter<Set<PlayerClassPermission>, List<Config>> {

    @Override
    public Set<PlayerClassPermission> convertToField(List<Config> value) {
        Set<PlayerClassPermission> set = new TreeSet<>();
        for (Config config : value) {
            set.add(new ObjectConverter().toObject(config, PlayerClassPermission::new));
        }
        return set;
    }

    @Override
    public List<Config> convertFromField(Set<PlayerClassPermission> value) {
        List<Config> list = new ArrayList<>();
        for (PlayerClassPermission playerClassPermission : value) {
            list.add(new ObjectConverter().toConfig(playerClassPermission, Config::inMemory));
        }
        return list;
    }

}
