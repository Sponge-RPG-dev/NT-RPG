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

package cz.neumimto.rpg.damage;

import com.google.common.collect.Lists;
import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.skills.NDamageType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 4.8.15.
 */
@Singleton
public class DamageService {

	@Inject
	private EntityService entityService;

	@Inject
	private CharacterService characterService;

	@Inject
	private ClassService classService;

	private Map<Double, TextColor> doubleColorMap = new TreeMap<>();

	private TextColor[] colorScale = new TextColor[]{
			TextColors.WHITE,
			TextColors.YELLOW,
			TextColors.GOLD,
			TextColors.RED,
			TextColors.DARK_RED,
			TextColors.DARK_PURPLE,
			TextColors.GRAY
	};

	public double getCharacterItemDamage(IActiveCharacter character, RpgItemType type) {
		if (character.isStub() || type == null) {
			return 1;
		}
		double base = character.getBaseWeaponDamage(type);

		for (Integer i : type.getWeaponClass().getProperties()) {
			base += entityService.getEntityProperty(character, i);
		}

		if (!type.getWeaponClass().getPropertiesMults().isEmpty()) {
			double totalMult = 0;
			for (Integer integer : type.getWeaponClass().getPropertiesMults()) {
				totalMult += entityService.getEntityProperty(character, integer);
			}
			base *= totalMult;
		}
		return base;
	}

	public double getCharacterProjectileDamage(IActiveCharacter character, EntityType type) {
		if (character.isStub() || type == null) {
			return 1;
		}
		double base = character.getBaseProjectileDamage(type)
				+ entityService.getEntityProperty(character, DefaultProperties.projectile_damage_bonus);
		if (type == EntityTypes.SPECTRAL_ARROW || type == EntityTypes.TIPPED_ARROW) {
			base *= entityService.getEntityProperty(character, DefaultProperties.arrow_damage_mult);
		} else {
			base *= entityService.getEntityProperty(character, DefaultProperties.other_projectile_damage_mult);
		}
		return base;
	}

	public void recalculateCharacterWeaponDamage(IActiveCharacter character) {
		if (character.isStub()) {
			return;
		}
		RpgItemStack mainHand = character.getMainHand();
		recalculateCharacterWeaponDamage(character, mainHand);
	}

	public void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemStack mainHand) {
		if (character.isStub()) {
			return;
		}
		if (mainHand == null) {
			character.setWeaponDamage(0);
		} else {
			recalculateCharacterWeaponDamage(character, mainHand.getItemType());
		}
	}

	public void recalculateCharacterWeaponDamage(IActiveCharacter character, RpgItemType type) {
		double damage = getCharacterItemDamage(character, type);
		// damage += character.getMainHand().getDamage() + character.getOffHand().getDamage();
		character.setWeaponDamage(damage);
	}

	public double getEntityResistance(IEntity entity, DamageType source) {
		if (source == DamageTypes.ATTACK) {
			return entityService.getEntityProperty(entity, DefaultProperties.physical_damage_protection_mult);
		}
		if (source == DamageTypes.MAGIC) {
			return entityService.getEntityProperty(entity, DefaultProperties.magic_damage_protection_mult);
		}
		if (source == DamageTypes.FIRE) {
			return entityService.getEntityProperty(entity, DefaultProperties.fire_damage_protection_mult);
		}
		if (source == NDamageType.LIGHTNING) {
			return entityService.getEntityProperty(entity, DefaultProperties.lightning_damage_protection_mult);
		}
		if (source == NDamageType.ICE) {
			return entityService.getEntityProperty(entity, DefaultProperties.ice_damage_protection_mult);
		}
		return 1;
	}

	public double getEntityDamageMult(IEntity entity, DamageType source) {
		if (source == DamageTypes.ATTACK) {
			return entityService.getEntityProperty(entity, DefaultProperties.physical_damage_bonus_mult);
		}
		if (source == DamageTypes.MAGIC) {
			return entityService.getEntityProperty(entity, DefaultProperties.magic_damage_bonus_mult);
		}
		if (source == DamageTypes.FIRE) {
			return entityService.getEntityProperty(entity, DefaultProperties.fire_damage_bonus_mult);
		}
		if (source == NDamageType.LIGHTNING) {
			return entityService.getEntityProperty(entity, DefaultProperties.lightning_damage_bonus_mult);
		}
		if (source == NDamageType.ICE) {
			return entityService.getEntityProperty(entity, DefaultProperties.ice_damage_bonus_mult);
		}
		return 1;
	}

	public void createDamageToColorMapping() {
		Collection<ClassDefinition> classes = classService.getClassDefinitions();
		Set<Double> list = new TreeSet<>();

		for (ClassDefinition aClass : classes) {
			Set<ClassItem> classItems = aClass.getWeapons();
			list = classItems.stream().map(ClassItem::getDamage).collect(Collectors.toCollection(TreeSet::new));
		}


		int size = list.size();
		if (size >= colorScale.length) {
			int l = list.size() / colorScale.length;
			int w = 0;
			for (List<Double> partition : Lists.partition(new ArrayList<>(list), l + 1)) {
				OptionalDouble max = partition.stream().mapToDouble(d -> d).max();
				doubleColorMap.put(max.getAsDouble(), colorScale[w]);
				w++;
			}
		}
	}

	public TextColor getColorByDamage(Double damage) {
		if (doubleColorMap.size() != colorScale.length) {
			return TextColors.RED;
		}
		TextColor val = TextColors.RED;
		for (Map.Entry<Double, TextColor> aDouble : doubleColorMap.entrySet()) {
			if (damage <= aDouble.getKey() || aDouble.getValue() == colorScale[colorScale.length - 1]) {
				val = aDouble.getValue();
			}
		}
		return val;
	}

}
