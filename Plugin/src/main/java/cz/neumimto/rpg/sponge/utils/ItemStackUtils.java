/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.sponge.utils;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import cz.neumimto.rpg.inventory.items.ItemMetaTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.spongepowered.api.item.ItemTypes.*;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
	/*If you want to add custom type of sword/axe/armor... via mod or resourcepack(remodeled potatoes) put them into these collections */

	public static Set<ItemType> consumables = new HashSet<ItemType>() {{
		addAll(Arrays.asList(APPLE,
				GOLDEN_APPLE,
				BAKED_POTATO,
				CARROT, POTION, BREAD, POTATO,
				POISONOUS_POTATO, ROTTEN_FLESH, PORKCHOP, COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON,
				COOKIE, COOKED_RABBIT, COOKED_FISH, FISH, CHICKEN, MELON));
	}};

	public static Set<ItemType> boots = new HashSet<ItemType>() {{
		addAll(Arrays.asList(DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS));
	}};

	public static Set<ItemType> chestplates = new HashSet<ItemType>() {{
		addAll(Arrays.asList(DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE,
				CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE));
	}};

	public static Set<ItemType> leggings = new HashSet<ItemType>() {{
		addAll(Arrays.asList(DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS,
				CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS));
	}};

	public static Set<ItemType> helmet = new HashSet<ItemType>() {{
		addAll(Arrays.asList(DIAMOND_HELMET, GOLDEN_HELMET,
				IRON_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET));
	}};

	public static Set<ItemType> any_armor = new HashSet<>();

	protected static String DAMAGE = "damage";
	protected static String DISPLAY_NAME = "name";

	protected static GlobalScope globalScope = NtRpgPlugin.GlobalScope;
	private static BiFunction<String, String, String> formatedConfig = (k, v) -> Utils.newLine(k + ": " + v + ";");
	private static Pattern pattern = Pattern.compile("\\((.*?)\\)");

	public static boolean isHelmet(String type) {
		return helmet.contains(type);
	}

	public static boolean isChestplate(String type) {
		return chestplates.contains(type);
	}

	public static boolean isLeggings(String type) {
		return leggings.contains(type);
	}

	public static boolean isBoots(String type) {
		return boots.contains(type);
	}

	public static boolean isConsumable(ItemType type) {
		return consumables.contains(type);
	}

	public static boolean hasSockets(ItemStack itemStack) {
		return globalScope.runewordService.getSocketCount(itemStack) > 0;
	}

	/**
	 * https://github.com/SpongePowered/SpongeForge/issues/470
	 *
	 * @param itemStack
	 */
	public static void createEnchantmentGlow(ItemStack itemStack) {
		itemStack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());
	}

	public static boolean isCharm(ItemStack is) {
		Optional<ItemMetaType> itemMetaType = is.get(NKeys.ITEM_META_TYPE);
		return itemMetaType.map(itemMetaType1 -> itemMetaType1.getId().equalsIgnoreCase(ItemMetaTypes.CHARM.getId())).orElse(false);
	}

	public static void dropItem(Player p, ItemStack itemStack) {
		Entity optional = p.getLocation().getExtent()
				.createEntity(EntityTypes.ITEM, p.getLocation()
						.getPosition()
						.add(TrigMath.cos((p.getRotation().getX() - 90) % 360) * 0.2, 1,
								TrigMath.sin((p.getRotation().getX() - 90) % 360) * 0.2));
		Vector3d rotation = p.getRotation();
		Vector3d direction = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
		Item item = (Item) optional;
		item.offer(Keys.VELOCITY, direction.mul(0.33));
		item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
		item.offer(Keys.PICKUP_DELAY, 50);
		p.getLocation().getExtent().spawnEntity(item);

	}

}
