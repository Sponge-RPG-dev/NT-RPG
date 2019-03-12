package cz.neumimto.rpg.commands.item;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.properties.PropertyService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class InspectItemDamageExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = args.<Player>getOne("player").get();
		ItemService is = IoC.get().build(ItemService.class);
		Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!itemInHand.isPresent()) {
			src.sendMessage(Text.of(player.getName() + " has no item in main hand"));
			return CommandResult.empty();
		}
		ItemStack itemStack = itemInHand.get();
		RPGItemType fromItemStack = is.getFromItemStack(itemStack);
		WeaponClass weaponClass = fromItemStack.getWeaponClass();
		List<WeaponClass> parents = new LinkedList<>();
		WeaponClass parent = weaponClass.getParent();
		List<Integer> o = new ArrayList<>();
		o.addAll(weaponClass.getProperties());
		o.addAll(weaponClass.getPropertiesMults());
		while (parent != null) {
			parents.add(parent);
			o.addAll(parent.getPropertiesMults());
			o.addAll(parent.getProperties());
			parent = parent.getParent();
		}
		parents.add(weaponClass);
		Collections.reverse(parents);

		List<Text> a = new ArrayList<>();
		for (WeaponClass wc : parents) {
			a.addAll(TO_TEXT.apply(wc));
		}
		for (Text text : a) {
			src.sendMessage(text);
		}
		src.sendMessage(Text.of(TextColors.GOLD, "=================="));
		DamageService ds = IoC.get().build(DamageService.class);
		CharacterService cs = IoC.get().build(CharacterService.class);
		PropertyService ps = IoC.get().build(PropertyService.class);
		IActiveCharacter character = cs.getCharacter(player);
		src.sendMessage(Text.of(TextColors.RED, "Damage: ", ds.getCharacterItemDamage(character, fromItemStack)));
		src.sendMessage(Text.of(TextColors.RED, "Details: "));
		src.sendMessage(Text.of(TextColors.GRAY, " - From Item: ", character.getBaseWeaponDamage(fromItemStack)));

		Collection<PlayerClassData> values = character.getClasses().values();
		for (PlayerClassData value : values) {
			Set<ConfigRPGItemType> configRPGItemTypes = value.getClassDefinition().getWeapons().get(itemInHand.get());
			if (configRPGItemTypes != null) {
				for (ConfigRPGItemType w : configRPGItemTypes) {
					if (w.rpgItemType.equals(fromItemStack)) {
						src.sendMessage(Text.of(TextColors.GRAY, "  - From Class: " + w.damage));
					}
				}
			}
		}


		src.sendMessage(Text.of(TextColors.GRAY, " - From WeaponClass: "));
		Iterator<Integer> iterator = o.iterator();
		while (iterator.hasNext()) {
			int integer = iterator.next();
			String nameById = ps.getNameById(integer);
			if (!nameById.endsWith("_mult")) {
				iterator.remove();
			} else continue;
			src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", cs.getCharacterProperty(character, integer)));
		}
		src.sendMessage(Text.of(TextColors.GRAY, "   - Mult: "));
		iterator = o.iterator();
		while (iterator.hasNext()) {
			int integer = iterator.next();
			String nameById = ps.getNameById(integer);
			src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", cs.getCharacterProperty(character, integer)));
		}

		return CommandResult.success();
	}

	private Function<WeaponClass, List<Text>> TO_TEXT = weaponClass -> {
		List<Text> list = new ArrayList<>();
		PropertyService ps = IoC.get().build(PropertyService.class);
		list.add(Text.of(TextColors.GOLD, weaponClass.getName()));
		for (Integer property : weaponClass.getProperties()) {
			list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
		}
		for (Integer property : weaponClass.getPropertiesMults()) {
			list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
		}
		return list;
	};
}
