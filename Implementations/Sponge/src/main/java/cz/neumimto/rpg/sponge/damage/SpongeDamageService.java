

package cz.neumimto.rpg.sponge.damage;

import com.google.common.collect.Lists;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.common.damage.AbstractDamageService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 4.8.15.
 */
@Singleton
public class SpongeDamageService extends AbstractDamageService<ISpongeCharacter, Living> {

    private Map<Double, TextColor> doubleColorMap = new TreeMap<>();

    private TextColor[] colorScale = new TextColor[]{
            TextColors.WHITE,
            TextColors.YELLOW,
            TextColors.GOLD,
            TextColors.RED,
            TextColors.DARK_RED,
            TextColors.DARK_PURPLE,
            TextColors.DARK_BLUE
    };

    public SpongeDamageService() {
        setDamageHandler(new SpongeDamageHandler());
    }

    public double getCharacterProjectileDamage(IActiveCharacter character, EntityType type) {
        if (character.isStub() || type == null) {
            return 1;
        }
        double base = character.getBaseProjectileDamage(type.getId())
                + entityService.getEntityProperty(character, CommonProperties.projectile_damage_bonus);
        if (type == EntityTypes.SPECTRAL_ARROW || type == EntityTypes.TIPPED_ARROW) {
            base *= entityService.getEntityProperty(character, CommonProperties.arrow_damage_mult);
        } else {
            base *= entityService.getEntityProperty(character, SpongeDefaultProperties.other_projectile_damage_mult);
        }
        return base;
    }

    @Override
    public void init() {
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

    @Override
    public void damageEntity(IEntity<Living> entity, double value) {
        entity.getEntity().damage(value, DamageSource.builder().absolute().type(DamageTypes.ATTACK).build());
    }

    public DamageType damageTypeById(String damageType) {
        return Sponge.getRegistry().getType(DamageType.class, damageType).orElseThrow(() -> new RuntimeException("Invalid damage type " + damageType));
    }

    public static class SpongeDamageHandler extends DamageHandler<ISpongeCharacter, Living> {

        @Override
        public boolean canDamage(ISpongeCharacter damager, Living damaged) {
            if (damager.getPlayer() == damaged) {
                return false;
            }
            if (damaged.getType() == EntityTypes.PLAYER) {
                if (damager.hasParty()) {
                    IActiveCharacter c = Rpg.get().getCharacterService().getCharacter(damaged.getUniqueId());
                    if (damager.getParty().getPlayers().contains(c)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public double getEntityResistance(IEntity entity, String damageType) {
            EntityService entityService = Rpg.get().getEntityService();
            DamageType source = Sponge.getRegistry().getType(DamageType.class, damageType).orElse(DamageTypes.CUSTOM);
            if (source == DamageTypes.ATTACK) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.physical_damage_protection_mult);
            }
            if (source == DamageTypes.MAGIC) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.magic_damage_protection_mult);
            }
            if (source == NDamageType.FIRE) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.fire_damage_protection_mult);
            }
            if (source == NDamageType.LIGHTNING) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.lightning_damage_protection_mult);
            }
            if (source == NDamageType.ICE) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.ice_damage_protection_mult);
            }
            return 1;
        }

        @Override
        public double getEntityDamageMult(IEntity entity, String damageType) {
            EntityService entityService = Rpg.get().getEntityService();
            DamageType source = Sponge.getRegistry().getType(DamageType.class, damageType).orElse(DamageTypes.CUSTOM);
            if (source == DamageTypes.ATTACK) {
                return entityService.getEntityProperty(entity, CommonProperties.physical_damage_bonus_mult);
            }
            if (source == DamageTypes.MAGIC) {
                return entityService.getEntityProperty(entity, CommonProperties.magic_damage_bonus_mult);
            }
            if (source == NDamageType.FIRE) {
                return entityService.getEntityProperty(entity, CommonProperties.fire_damage_bonus_mult);
            }
            if (source == NDamageType.LIGHTNING) {
                return entityService.getEntityProperty(entity, CommonProperties.lightning_damage_bonus_mult);
            }
            if (source == NDamageType.ICE) {
                return entityService.getEntityProperty(entity, SpongeDefaultProperties.ice_damage_bonus_mult);
            }
            return 1;
        }

    }

}
