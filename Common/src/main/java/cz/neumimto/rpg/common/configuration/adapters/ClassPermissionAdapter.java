package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassPermissionAdapter implements Converter<Set<PlayerClassPermission>, List<Config>> {

    @Override
    public Set<PlayerClassPermission> convertToField(List<Config> value) {
        Set<PlayerClassPermission> set = new TreeSet<>();
        if (value != null) {
            for (Config config : value) {
                set.add(new ObjectConverter().toObject(config, PlayerClassPermission::new));
            }
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
