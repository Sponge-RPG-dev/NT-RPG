package cz.neumimto.damage;
;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.utils.ItemStackUtils;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 4.8.15.
 */
@Singleton
public class DamageService {

    Map<ItemType,Short> map = new HashMap<>();
    public double getCharacterItemDamage(IActiveCharacter character, ItemType type) {
        if (character.isStub())
            return 0;
        double base = character.getBaseWeaponDamage(type)+character.getCharacterProperty(DefaultProperties.weapon_damage_bonus);
        base += character.getCharacterProperty(map.get(type));
        if (ItemStackUtils.isSword(type)) {
            base *= character.getCharacterProperty(DefaultProperties.swords_damage_mult);
        } else if (ItemStackUtils.isAxe(type)) {
            base *= character.getCharacterProperty(DefaultProperties.axes_damage_mult);
        } else if (ItemStackUtils.isPickaxe(type)) {
            base *= character.getCharacterProperty(DefaultProperties.pickaxes_damage_mult);
        } else if (ItemStackUtils.isHoe(type)) {
            base *= character.getCharacterProperty(DefaultProperties.hoes_damage_mult);
        } else if (ItemStackUtils.isBow(type)) {
            base *= character.getCharacterProperty(DefaultProperties.bows_meele_damage_mult);
        }
        return base;
    }

    //todo wait for sponge api and its damage cause types
    public double getCharacterResistance(IActiveCharacter character) {
     /*   if (source == DamageSource.magic)
            return character.getCharacterProperty(DefaultProperties.magic_damage_protection_mult);
        if (source == DamageSource.lightningBolt)
            return character.getCharacterProperty(DefaultProperties.lightning_damage_protection_mult);
        if (source == DamageSource.wither)
            return character.getCharacterProperty(DefaultProperties.wither_damage_protection_mult);
        if (source == DamageSource.onFire || source == DamageSource.inFire)
            return character.getCharacterProperty(DefaultProperties.fire_damage_protection_mult);
        return 1;*/
        return 1;
    }

    public double getCharacterProjectileDamage(IActiveCharacter character, ProjectileType type) {
        if (character.isStub())
            return 0;
        return 20;
    }

    @PostProcess(priority = 6)
    public void buildPropertiesMap() {
        map.put(ItemTypes.DIAMOND_SWORD,DefaultProperties.diamond_sword_bonus_damage);
        map.put(ItemTypes.GOLDEN_SWORD,DefaultProperties.golden_sword_bonus_damage);
        map.put(ItemTypes.IRON_SWORD,DefaultProperties.iron_sword_bonus_damage);
        map.put(ItemTypes.WOODEN_SWORD,DefaultProperties.wooden_sword_bonus_damage);

        map.put(ItemTypes.DIAMOND_AXE,DefaultProperties.diamond_axe_bonus_damage);
        map.put(ItemTypes.GOLDEN_AXE,DefaultProperties.golden_axe_bonus_damage);
        map.put(ItemTypes.IRON_AXE,DefaultProperties.iron_axe_bonus_damage);
        map.put(ItemTypes.WOODEN_AXE,DefaultProperties.wooden_axe_bonus_damage);

        map.put(ItemTypes.DIAMOND_PICKAXE,DefaultProperties.diamond_pickaxe_bonus_damage);
        map.put(ItemTypes.GOLDEN_PICKAXE,DefaultProperties.golden_pickaxe_bonus_damage);
        map.put(ItemTypes.IRON_PICKAXE,DefaultProperties.iron_pickaxe_bonus_damage);
        map.put(ItemTypes.WOODEN_PICKAXE,DefaultProperties.wooden_pickaxe_bonus_damage);


        map.put(ItemTypes.DIAMOND_HOE,DefaultProperties.diamond_hoe_bonus_damage);
        map.put(ItemTypes.GOLDEN_HOE,DefaultProperties.golden_hoe_bonus_damage);
        map.put(ItemTypes.IRON_HOE,DefaultProperties.iron_hoe_bonus_damage);
        map.put(ItemTypes.WOODEN_HOE,DefaultProperties.wooden_hoe_bonus_damage);

        map.put(ItemTypes.BOW,DefaultProperties.bow_meele_bonus_damage);
    }
}
