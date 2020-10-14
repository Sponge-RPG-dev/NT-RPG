package cz.neumimto.rpg.sponge.inventory;

import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import cz.neumimto.rpg.sponge.items.SpongeRpgItemType;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by NeumimTo on 29.4.2018.
 */
@Singleton
public class SpongeItemService extends AbstractItemService {

    @Inject
    private EffectService effectService;

    @Inject
    private SpongeRpgPlugin plugin;

    public Optional<RpgItemType> getRpgItemType(ItemStack itemStack) {
        Optional<Text> text = itemStack.get(Keys.DISPLAY_NAME);
        return getRpgItemType(itemStack.getType().getId(), text.map(Text::toPlain).orElse(null));
    }

    public Optional<RpgItemStack> getRpgItemStack(ItemStack itemStack) {
        return getRpgItemType(itemStack).map(a -> new RpgItemStackImpl(a,
                getItemEffects(itemStack),
                getItemBonusAttributes(itemStack),
                getItemMinimalAttributeRequirements(itemStack),
                getClassRequirements(itemStack),
                getItemData(itemStack)
        ));
    }

    private Map<String, Double> getItemData(ItemStack itemStack) {
        DataQuery query = DataQuery.of("UnsafeData", "AttributeModifiers");
        Optional<List<DataView>> containers = itemStack.toContainer().getViewList(query);
        if (containers.isPresent()) {
            List<DataView> dataViews = containers.get();
            Map<String, Double> map = new HashMap<>();
            for (DataView dataView : dataViews) {
                Optional<String> attributeName = dataView.getString(DataQuery.of("AttributeName"));
                if (attributeName.isPresent() && attributeName.get().equals("generic.attackDamage")) {
                    map.put(DAMAGE_KEY, dataView.getDouble(DataQuery.of("Amount")).get());
                }
            }
            return map;
        }
        return Collections.emptyMap();
    }

    private Map<ClassDefinition, Integer> getClassRequirements(ItemStack itemStack) {
        return Collections.emptyMap();
    }

    private Map<AttributeConfig, Integer> getItemMinimalAttributeRequirements(ItemStack itemStack) {
        Optional<Map<String, Integer>> req = itemStack.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
        if (req.isPresent()) {
            return parseItemAttributeMap(req.get());
        }
        return super.itemAttributesPlaceholder;
    }

    private Map<AttributeConfig, Integer> getItemBonusAttributes(ItemStack itemStack) {
        Optional<Map<String, Integer>> req = itemStack.get(NKeys.ITEM_ATTRIBUTE_BONUS);
        if (req.isPresent()) {
            return parseItemAttributeMap(req.get());
        }
        return super.itemAttributesPlaceholder;
    }

    public Map<IGlobalEffect, EffectParams> getItemEffects(ItemStack is) {
        Optional<Map<String, EffectParams>> q = is.get(NKeys.ITEM_EFFECTS);
        if (q.isPresent()) {
            return effectService.parseItemEffects(q.get());
        }
        return Collections.emptyMap();
    }

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass wClass) {
        Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, parsed.itemId);
        if (!type.isPresent()) {
            Log.error(" - Not Managed ItemType " + parsed.itemId);
            return Optional.empty();
        }
        ItemType itemType = type.get();
        return Optional.of(new SpongeRpgItemType(parsed.itemId, parsed.variant, wClass, parsed.damage, parsed.armor, itemType));
    }


    @Override
    public Set<String> getAllItemIds() {
        return Sponge.getRegistry().getAllOf(ItemType.class)
                .stream()
                .map(CatalogType::getId)
                .collect(Collectors.toSet());

    }
}
