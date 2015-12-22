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

import cz.neumimto.IEntity;
import cz.neumimto.IEntityType;
import cz.neumimto.ResourceLoader;
import cz.neumimto.Weapon;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.damage.DamageService;
import cz.neumimto.damage.ISkillDamageSource;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.entities.EntityService;
import cz.neumimto.entities.IMob;
import cz.neumimto.entities.NEntity;
import cz.neumimto.events.character.PlayerCombatEvent;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import cz.neumimto.utils.ItemStackUtils;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.Living;
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
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Texts;
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

    @Inject
    private EntityService entityService;


    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Auth event) {
        if (event.isCancelled())
            return;
        UUID id = event.getProfile().getUniqueId();
        characterService.loadPlayerData(id);
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        IActiveCharacter character = characterService.getCharacter(event.getTargetEntity().getUniqueId());
        characterService.assignPlayerToCharacter(event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        IActiveCharacter character = characterService.removeCachedWrapper(player.getUniqueId());
        if (!character.isStub()) {
            Location loc = player.getLocation();
            World ex = (World) loc.getExtent();
            character.updateLastKnownLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockY(), ex.getName());
            characterService.putInSaveQueue(character.getCharacterBase());
            effectService.removeAllEffects(character);
            /*Always reset the persistent properties back to vanilla values in a case
             some dummy decides to remove my awesome plugin :C */
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

    public void onEntitySpawn(RespawnPlayerEvent event) {

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
                    .forEach(iEffect -> effectService.removeEffect(iEffect, character));
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
                    effectService.applyGlobalEffectsAsEnchantments(weapon.getEffects(), character);
                    damageService.recalculateCharacterWeaponDamage(character);
                }
            }
        }
    }

    @Listener(order = Order.BEFORE_POST)
    public void onAttack(InteractEntityEvent.Primary event) {
        if (event.isCancelled())
            return;
        if (event.getTargetEntity().getType() == EntityTypes.PLAYER) {
            IActiveCharacter target = characterService.getCharacter(event.getTargetEntity().getUniqueId());
            if (target.isStub() && !PluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS) {
                event.setCancelled(true);
                return;
            }
            Optional<Player> first = event.getCause().first(Player.class);
            if (first.isPresent()) {
                Player player = first.get();
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.getParty() == target.getParty() && !character.getParty().isFriendlyfire()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onEntityDespawn(DestructEntityEvent event) {
        if (event.getTargetEntity().get(Keys.HEALTH).isPresent()) {
            if (event.getTargetEntity() != EntityTypes.PLAYER)
                entityService.remove(event.getTargetEntity().getUniqueId());
        }
    }
/*
//TODO fix memoryleak
    @Listener
    public void onChunkDespawn(UnloadChunkEvent event) {

        entityService.remove(event.getTargetChunk().getEntities(Utils::isLivingEntity));
    }
*/
    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
        player.offer(Keys.HEALTH_SCALE,PluginConfig.HEALTH_SCALE);
    }

    @Listener
    public void onPreDamage(DamageEntityEvent event) {
        final Cause cause = event.getCause();
        Optional<EntityDamageSource> first = cause.first(EntityDamageSource.class);
        if (first.isPresent()) {

            Entity targetEntity = event.getTargetEntity();
            EntityDamageSource entityDamageSource = first.get();
            Entity source = entityDamageSource.getSource();
            if (source.get(Keys.HEALTH).isPresent()) {
                //attacker
                IEntity entity = entityService.get((Living) source);
                double newdamage = 0;
                if (entity.getType() == IEntityType.CHARACTER) {
                    IActiveCharacter character = (IActiveCharacter) entity;
                    //TODO move this into EntityEquipmentEvent once its implemented!
                    //its inefficient to recalculate damage every time player attacks someone
                    damageService.recalculateCharacterWeaponDamage(character);
                    //
                    newdamage = character.getWeaponDamage();
                    newdamage *= damageService.getCharacterBonusDamage(character, entityDamageSource.getType());
                } else {
                    if (!PluginConfig.OVERRIDE_MOBS) {
                        newdamage = entityService.getMobDamage(entity.getType());
                    }
                }
                //defende
                /*
                if (targetEntity.getType() == EntityTypes.PLAYER) {
                    IActiveCharacter tcharacter = characterService.getCharacter(targetEntity.getUniqueId());
                    double armor = tcharacter.getArmorValue();
                    final double damagefactor = damageService.DamageArmorReductionFactor.apply(newdamage, armor);
                    PlayerCombatEvent ce = new PlayerCombatEvent(character, tcharacter, damage, damagefactor);
                    event.setBaseDamage(ce.getDamage());
                    event.setDamage(DamageModifier.builder().cause(Cause.ofNullable(null)).type(DamageModifierTypes.ARMOR).build(), input -> input * ce.getDamagefactor());
                }*/
                event.setBaseDamage(newdamage);
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
                double finalDamage = damageService.getSkillDamage(caster, skill.getDamageType()) * damageService.getCharacterBonusDamage(caster, type);
                event.setBaseDamage(finalDamage);
                if (event.getTargetEntity().getType() == EntityTypes.PLAYER) {
                    IActiveCharacter targetchar = characterService.getCharacter(event.getTargetEntity().getUniqueId());
                    double target_resistence = damageService.getCharacterResistance(targetchar, type);
                    event.setDamage(DamageModifier.builder().cause(Cause.ofNullable(null)).type(DamageModifierTypes.MAGIC).build(), input -> input * target_resistence);
                }
            }
        }
    }

}
