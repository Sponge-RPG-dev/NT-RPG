package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.Log;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class AllowedArmorListAdapter implements TypeSerializer<Set<RPGItemType>> {

    @Override
    public Set<RPGItemType> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
        List<String> items = configurationNode.getList(TypeToken.of(String.class));
        Set<RPGItemType> res = new HashSet<>();
        for (String a : items) {
            String[] split = a.split(";");
            Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, split[0]);
            if (type.isPresent()) {
                RPGItemType rpgItemType = new RPGItemType(type.get(), split.length == 2 ? split[1] : null, WeaponClass.ARMOR, 0);
                res.add(rpgItemType);
            } else {
                Log.warn(" - Unknown item \""+a+"\")");
            }
        }
        return res;
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Set<RPGItemType> rpgItemTypes, ConfigurationNode configurationNode) {

    }
}
