package cz.neumimto.rpg.api.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.config.blackjack.and.hookers.annotations.EnableSetterInjection;
import cz.neumimto.config.blackjack.and.hookers.annotations.Setter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 5.1.2019.
 */
@EnableSetterInjection
public class ClassRpgItemTypeAdapter implements TypeSerializer<Set<ClassItem>> {

    private IEffectSourceProvider provider;

    @Setter
    public void setProvider(IEffectSourceProvider provider) {
        this.provider = provider;
    }

    @Override
    public Set<ClassItem> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode)
            throws ObjectMappingException {

        List<String> list = configurationNode.getList(TypeToken.of(String.class));

        Map<RpgItemType, Double> map = new HashMap<>();
        Map<RpgItemType, Double> map2 = new HashMap<>();

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.toLowerCase().startsWith("weaponclass:")) {
                String[] data = s.split(":");
                String clazz = data[1];
                Set<RpgItemType> itemTypes = Rpg.get().getItemService().getItemTypesByWeaponClass(clazz);
                for (RpgItemType itemType : itemTypes) {
                    map.put(itemType, itemType.getDamage());
                }
            } else {
                ItemString parsed = ItemString.parse(s);
                Optional<RpgItemType> rpgItemType = Rpg.get().getItemService().getRpgItemType(parsed.itemId, parsed.model);
                if (rpgItemType.isPresent()) {
                    RpgItemType rpgItemType1 = rpgItemType.get();
                    map2.put(rpgItemType1, parsed.damage);
                } else {
                    Log.error("- Not managed item type " + RpgItemType.KEY_BUILDER.apply(parsed.itemId, parsed.model));
                }
            }
        }

        map.putAll(map2);

        return map.entrySet()
                .stream()
                .map(a -> Rpg.get().getItemService().createClassItemSpecification(a.getKey(), a.getValue(), provider))
                .collect(Collectors.toSet());
    }

    @Override
    public void serialize(TypeToken<?> typeToken, Set<ClassItem> classItems, ConfigurationNode configurationNode) {
        List<String> toSerialize = new ArrayList<>();

        for (ClassItem classItem : classItems) {
            RpgItemType type = classItem.getType();
            double damage = classItem.getDamage();
            toSerialize.add(type.getKey().concat(";").concat(String.valueOf(damage)));
        }

        configurationNode.setValue(toSerialize);
    }
}

