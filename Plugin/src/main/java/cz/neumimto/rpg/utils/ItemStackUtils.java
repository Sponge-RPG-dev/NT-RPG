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

package cz.neumimto.rpg.utils;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.ItemRestriction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.spongepowered.api.item.ItemTypes.*;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
	/*If you want to add custom type of sword/axe/armor... via mod or resourcepack(remodeled potatoes) put them into these collections */
	public static Set<ItemType> swords = new HashSet<ItemType>() {{
		add(DIAMOND_SWORD);
		add(GOLDEN_SWORD);
		add(IRON_SWORD);
		add(STONE_SWORD);
		add(WOODEN_SWORD);
	}};

	public static Set<ItemType> shovels = new HashSet<ItemType>() {{
		add(DIAMOND_SHOVEL);
		add(GOLDEN_SHOVEL);
		add(IRON_SHOVEL);
		add(STONE_SHOVEL);
		add(WOODEN_SHOVEL);
	}};

	public static Set<ItemType> axes = new HashSet<ItemType>() {{
		add(DIAMOND_AXE);
		add(GOLDEN_AXE);
		add(IRON_AXE);
		add(STONE_AXE);
		add(WOODEN_AXE);
	}};
	public static Set<ItemType> pickaxes = new HashSet<ItemType>() {{
		add(DIAMOND_PICKAXE);
		add(GOLDEN_PICKAXE);
		add(IRON_PICKAXE);
		add(STONE_PICKAXE);
		add(WOODEN_PICKAXE);
	}};
	public static Set<ItemType> hoes = new HashSet<ItemType>() {{
		add(DIAMOND_HOE);
		add(GOLDEN_HOE);
		add(IRON_HOE);
		add(STONE_HOE);
		add(WOODEN_HOE);
	}};
	public static Set<ItemType> bows = new HashSet<ItemType>() {{
		add(BOW);
	}};
	public static Set<ItemType> staffs = new HashSet<ItemType>() {{
		add(BLAZE_ROD);
		add(STICK);
	}};
	public static Set<ItemType> weapons = new HashSet<ItemType>() {{
		addAll(swords);
		addAll(axes);
		addAll(bows);
		addAll(pickaxes);
		addAll(hoes);
		addAll(shovels);
	}};
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

	public static Map<String, ItemRestriction> restrictionMap = new HashMap<>();

	protected static String ID = "id";
	protected static String QUANTITY = "quantity";
	protected static String DAMAGE = "damage";
	protected static String DISPLAY_NAME = "name";
	protected static String LORE = "lore";
	protected static GlobalScope globalScope = NtRpgPlugin.GlobalScope;
	private static BiFunction<String, String, String> formatedConfig = (k, v) -> Utils.newLine(k + ": " + v + ";");
	private static Pattern pattern = Pattern.compile("\\((.*?)\\)");

	public static boolean isSword(ItemType type) {
		return swords.contains(type);
	}

	public static boolean isAxe(ItemType type) {
		return axes.contains(type);
	}

	public static boolean isPickaxe(ItemType type) {
		return pickaxes.contains(type);
	}

	public static boolean isHoe(ItemType type) {
		return hoes.contains(type);
	}

	public static boolean isBow(ItemType type) {
		return bows.contains(type);
	}

	public static boolean isWeapon(ItemType type) {
		return weapons.contains(type);
	}

	public static boolean isStaff(ItemType type) {
		return staffs.contains(type);
	}

	public static boolean isHelmet(ItemType type) {
		return helmet.contains(type);
	}

	public static boolean isChestplate(ItemType type) {
		return chestplates.contains(type);
	}

	public static boolean isLeggings(ItemType type) {
		return leggings.contains(type);
	}

	public static boolean isBoots(ItemType type) {
		return boots.contains(type);
	}

	public static boolean isItemSkillBind(ItemStack is) {
		if (is.getType() != InventoryService.ITEM_SKILL_BIND) {
			return false;
		}
		Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
		if (texts.isPresent()) {
			List<Text> a = texts.get();
			if (a.size() > 1) {
				Text text = a.get(0);
				if (text.toPlain().equalsIgnoreCase(Localization.SKILLBIND)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void createProperty(StringBuilder builder, String value, String key) {
		if (key != null)
			builder.append(value).append(":").append(key).append(";");
	}

	private static void createProperty(StringBuilder builder, String value, Text key) {
		if (key != null)
			createProperty(builder, value, key.toString());
	}

	private static void createProperty(StringBuilder b, String value, int key) {
		createProperty(b, value, String.valueOf(key));
	}


	public static boolean isConsumable(ItemType type) {
		return consumables.contains(type);
	}

	public static boolean isItemRune(ItemStack is) {
		Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
		if (texts.isPresent()) {
			List<Text> a = texts.get();
			if (a.size() >= 1) {
				Text text = a.get(0);
				String s = text.toPlain();
				if (s.equalsIgnoreCase(Localization.RUNE)) {
					return true;
				}
			}
		}
		return false;
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
		Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
		if (texts.isPresent()) {
			List<Text> texts1 = texts.get();
			if (texts1.size() > 1) {
				String s = texts1.get(1).toPlain();
				if (s.equalsIgnoreCase(Localization.CHARM)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getItemLevel(Text text) {
		String s = text.toPlain();
		String[] split = s.split(":");
		if (split.length > 1) {
			return Integer.parseInt(split[1]);
		}
		return 0;
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

	static {
		any_armor.addAll(helmet);
		any_armor.addAll(chestplates);
		any_armor.addAll(leggings);
		any_armor.addAll(boots);
	}

	public static boolean checkType(ItemType i, String item) {
		if (item.equalsIgnoreCase("sword")) {
			return swords.contains(i);
		}
		if (item.equalsIgnoreCase("axe")) {
			return axes.contains(i);
		}
		if (item.equalsIgnoreCase("pickaxe")) {
			return pickaxes.contains(i);
		}
		if (item.equalsIgnoreCase("hoe")) {
			return hoes.contains(i);
		}
		if (item.equalsIgnoreCase("staff")) {
			return staffs.contains(i);
		}
		return false;
	}

	public static Text stringToItemTooltip(String string) {
		return Text.of(TextColors.GOLD, TextStyles.ITALIC, string);
	}

}
