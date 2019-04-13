package cz.neumimto.rpg.inventory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.items.SpongeRpgItemType;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


/**
 * Created by NeumimTo on 29.4.2018.
 */
@Singleton
public class SpongeItemService extends AbstractItemService {

	@Inject
	private EffectService effectService;

	public Optional<RpgItemType> getRpgItemType(ItemStack itemStack) {
		Optional<Text> text = itemStack.get(Keys.DISPLAY_NAME);
		return getRpgItemType(itemStack.getType().getId(), text.map(Text::toPlain).orElse(null));
	}

	public Optional<RpgItemStack> getRpgItemStack(ItemStack itemStack) {
		return getRpgItemType(itemStack).map(a -> new RpgItemStackImpl(a,
													getItemEffects(itemStack),
													getItemBonusAttributes(itemStack),
													getItemMinimalAttributeRequirements(itemStack),
													getClassRequirements(itemStack)
												));
	}

	private Map<ClassDefinition, Integer> getClassRequirements(ItemStack itemStack) {
		return Collections.emptyMap();
	}

	private Map<Attribute, Integer> getItemMinimalAttributeRequirements(ItemStack itemStack) {
        Optional<Map<String, Integer>> req = itemStack.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
        if (req.isPresent()) {
            return parseItemAttributeMap(req.get());
        }
        return super.itemAttributesPlaceholder;
	}

	private Map<Attribute, Integer> getItemBonusAttributes(ItemStack itemStack) {
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
	protected Optional<RpgItemType> createRpgItemType(ItemString parsed, WeaponClass wClass) {
		Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, parsed.itemId);
		if (!type.isPresent()) {
			Log.error(" - Not Managed ItemType " + parsed.itemId);
			return Optional.empty();
		}
		ItemType itemType = type.get();
		return Optional.of(new SpongeRpgItemType(parsed.itemId, parsed.model, wClass, parsed.damage, parsed.armor, itemType));
	}


	@Override
	public void loadItemGroups(Path path) {
		File f = path.toFile();
		if (!f.exists()) {
			Optional<Asset> asset = Sponge.getAssetManager().getAsset(NtRpgPlugin.GlobalScope.plugin, "ItemGroups.conf");
			if (!asset.isPresent()) {
				throw new IllegalStateException("Could not find an asset ItemGroups.conf");
			}
			try {
				asset.get().copyToFile(f.toPath());
			} catch (IOException e) {
				throw new IllegalStateException("Could not create ItemGroups.conf file", e);
			}
		}

		Config c = ConfigFactory.parseFile(path.toFile());
		loadItemGroups(c);
	}


}
