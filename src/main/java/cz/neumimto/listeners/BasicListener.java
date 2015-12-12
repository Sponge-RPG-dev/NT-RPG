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

package cz.neumimto.listeners;

import cz.neumimto.ResourceLoader;
import cz.neumimto.Weapon;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.damage.DamageService;
import cz.neumimto.damage.ISkillDamageSource;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.events.character.CharacterCombatEvent;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import cz.neumimto.utils.ItemStackUtils;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.ChangeEntityEquipmentEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 12.2.2015.
 */
@ResourceLoader.ListenerClass
public class BasicListener {

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private EffectService effectService;

    @Inject
    private DamageService damageService;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        if (event.isCancelled())
            return;
        UUID id = event.getProfile().getUniqueId();
        characterService.loadPlayerData(id);
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event)  {
        IActiveCharacter character = characterService.getCharacter(event.getTargetEntity().getUniqueId());
        if (PluginConfig.TELEPORT_PLAYER_TO_LAST_CHAR_LOCATION && !character.isStub()) {
            CharacterBase characterBase = character.getCharacterBase();
            Optional<World> world = game.getServer().getWorld(characterBase.getWorld());
            if (!world.isPresent())
                return;
            World w = world.get();
            Location<World> loc = new Location<World>(w,characterBase.getX(),characterBase.getY(),characterBase.getZ());
            event.getTargetEntity().setLocationSafely(loc);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        IActiveCharacter character = characterService.removeCachedWrapper(player.getUniqueId());
        if (!character.isStub()) {
            Location loc = player.getLocation();
            World ex = (World)loc.getExtent();
            character.updateLastKnownLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockY(),ex.getName());
            characterService.putInSaveQueue(character.getCharacterBase());
            character.getEffects().stream().forEach(effectService::purgeEffect);
            /*Always reset the persistent properties back to vanilla values in a case
             some dummy decides to remove my awesome plugin :C */
            //HP
            Utils.resetPlayerToDefault(player);
        }
    }

    @Listener
    public void onUserBan(BanUserEvent event) {
        if (PluginConfig.REMOVE_PLAYERDATA_AFTER_PERMABAN) {
            if (!event.getBan().getExpirationDate().isPresent()) {
                characterService.removePlayerData(event.getTargetUser().getUniqueId());
            }
        }
    }

    @Listener
    public void onEntityDestruct(DestructEntityEvent event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub())
                return;
            character.getEffects().stream()
                    .filter(iEffect1 -> iEffect1.getEffectSource() == EffectSource.TEMP && iEffect1.requiresRegister())
                    .forEach(iEffect -> effectService.removeEffect(iEffect,character));
        }
    }

    @Listener
    public void onItemChange(ChangeEntityEquipmentEvent.TargetPlayer event) {
        Player player = event.getTargetEntity();
        if (event.getItemStack().isPresent()) {
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            Transaction<ItemStackSnapshot> itemStackSnapshotTransaction = event.getItemStack().get();
            ItemStackSnapshot finalSnapshot = itemStackSnapshotTransaction.getFinal();
            if (ItemStackUtils.isWeapon(finalSnapshot.getType())) {
                if (characterService.canUseItemType(character, finalSnapshot.getType())) {
                    //remove old
                    Weapon weapon = character.getMainHand();
                    if (weapon != Weapon.EmptyHand) {
                        Map<IGlobalEffect, Integer> effects = weapon.getEffects();
                        effectService.removeGlobalEffectsAsEnchantments(effects, character);
                    }
                    //add new
                    weapon = ItemStackUtils.itemStackToWeapon(finalSnapshot.createStack());
                    character.setMainHand(weapon);
                    effectService.applyGlobalEffectsAsEnchantments(weapon.getEffects(),character);
                    damageService.recalculateCharacterWeaponDamage(character);
                }
            }
        }
    }

    @Listener(order = Order.BEFORE_POST)
    public void onAttack(InteractEntityEvent.Primary event) {
        event.getGame();
    }

    @Listener
    public void onPreDamage(DamageEntityEvent event) {
        final Cause cause = event.getCause();
        Optional<EntityDamageSource> first = cause.first(EntityDamageSource.class);
        if (first.isPresent()) {
            Entity targetEntity = event.getTargetEntity();
            EntityDamageSource entityDamageSource = first.get();
            Entity source = entityDamageSource.getSource();
            if (source.getType() == EntityTypes.PLAYER) {
                Player player = (Player) source;
                IActiveCharacter character = characterService.getCharacter(source.getUniqueId());
                Optional<ItemStack> itemInHand = player.getItemInHand();
                if (itemInHand.isPresent()) {
                    double damage = character.getWeaponDamage();
                    if (targetEntity.getType() == EntityTypes.PLAYER) {
                        IActiveCharacter tcharacter = characterService.getCharacter(targetEntity.getUniqueId());
                        double armor = character.getArmorValue();
                        final double damagefactor = damageService.DamageArmorReductionFactor.apply(damage, armor);
                        CharacterCombatEvent ce = new CharacterCombatEvent(character,tcharacter,damage,damagefactor);

                        event.setBaseDamage(ce.getDamage());
                        event.setDamage(DamageModifier.builder().cause(Cause.of()).type(DamageModifierTypes.ARMOR).build(), input -> input*ce.getDamagefactor());
                    }
                }

            }
        }

        Optional<ISkillDamageSource> skilldamage = cause.first(ISkillDamageSource.class);
        if (skilldamage.isPresent()) {
            ISkillDamageSource iSkillDamageSource = skilldamage.get();
            IActiveCharacter caster = iSkillDamageSource.getCaster();
            ISkill skill = iSkillDamageSource.getSkill();
            DamageType type = skill.getDamageType();

            if (caster.hasPreferedDamageType()) {
                type = caster.getDamageType();
            }
            double finalDamage = damageService.getCharacterBonusDamage(caster,type);
            event.setBaseDamage(finalDamage);
            if (event.getTargetEntity().getType() == EntityTypes.PLAYER) {
                IActiveCharacter targetchar = characterService.getCharacter(event.getTargetEntity().getUniqueId());
                double target_resistence = damageService.getCharacterResistance(targetchar,type);
                event.setDamage(DamageModifier.builder().cause(Cause.of()).type(DamageModifierTypes.MAGIC).build(), input -> input*target_resistence);
            }
        }
    }
}
