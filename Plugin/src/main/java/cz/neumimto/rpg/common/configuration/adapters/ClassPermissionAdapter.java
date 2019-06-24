package cz.neumimto.rpg.common.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClassPermissionAdapter implements TypeSerializer<Set<PlayerClassPermission>> {

    @Override
    public Set<PlayerClassPermission> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
        List<PlayerClassPermission> list = configurationNode.getList(TypeToken.of(PlayerClassPermission.class));
        return new TreeSet<>(list);
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Set<PlayerClassPermission> playerClassPermissions, ConfigurationNode configurationNode) throws ObjectMappingException {
        configurationNode.setValue(playerClassPermissions);
    }
}
