package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassPermission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassResourceAdapter implements Converter<Set<ClassResource>, List<Config>> {
    @Override
    public Set<ClassResource> convertToField(List<Config> value) {
        Set<ClassResource> set = new HashSet();
        if (value != null) {
            for (Config config : value) {
                set.add(new ObjectConverter().toObject(config, ClassResource::new));
            }
        }
        return set;
    }

    @Override
    public List<Config> convertFromField(Set<ClassResource> value) {
        List<Config> list = new ArrayList<>();
        for (ClassResource r : value) {
            list.add(new ObjectConverter().toConfig(r, Config::inMemory));
        }
        return list;
    }
}
