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
import cz.neumimto.inventory.Weapon;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.damage.DamageService;
import cz.neumimto.damage.ISkillDamageSource;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.entities.EntityService;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.ExperienceSource;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ExtendedSkillInfo;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.ProjectileProperties;
import cz.neumimto.skills.SkillService;
import cz.neumimto.utils.ItemStackUtils;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

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

    @Inject
    private SkillService skillService;

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
            character.getCharacterBase().setLastKnownPlayerName(event.getTargetEntity().getName());
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

    @Listener
    public void onEntitySpawn(DestructEntityEvent event) {
        Entity targetEntity = event.getTargetEntity();
    }

    @Listener
    public void onEntityDestruct(DestructEntityEvent.Death event) {
        Entity targetEntity = event.getTargetEntity();
        if (targetEntity.getType() == EntityTypes.PLAYER) {
            IActiveCharacter character = characterService.getCharacter(targetEntity.getUniqueId());
            if (character.isStub())
                return;
            //todo death penalization
            character.getEffects().stream()
                    .filter(iEffect1 -> iEffect1.getEffectSource() == EffectSource.TEMP && iEffect1.requiresRegister())
                    .forEach(iEffect -> effectService.removeEffect(iEffect, character));
        } else {
            Optional<EntityDamageSource> first = event.getCause().first(EntityDamageSource.class);
            if (first.isPresent()) {
                EntityDamageSource entityDamageSource = first.get();
                entityService.remove(targetEntity.getUniqueId());
                double exp = entityService.getExperiences(targetEntity.getType());
                //todo share in party
                IEntity source = entityService.get(entityDamageSource.getSource());
                if (source.getType() == IEntityType.CHARACTER) {
                    IActiveCharacter character = characterService.getCharacter(first.get().getSource().getUniqueId());
                    characterService.addExperiences(character, exp, ExperienceSource.PVE);
                }
            }
        }
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            for (SlotTransaction slotTransaction : event.getTransactions()) {
                System.out.print(slotTransaction.getSlot());
            }
        }
    }

    @Listener
    public void onItemEquip(ChangeInventoryEvent.Transfer event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            for (SlotTransaction slotTransaction : event.getTransactions()) {
                System.out.print(slotTransaction.getSlot());
            }
        }
    }

    @Listener
    public void onItemEquip(ChangeInventoryEvent.Held event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            for (SlotTransaction slotTransaction : event.getTransactions()) {
                System.out.print("HELD" + slotTransaction.getSlot());
            }
        }
    }

    @Listener
    public void onItemChange(ChangeInventoryEvent.Held event) {
        List<SlotTransaction> transactions = event.getTransactions();
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {

        }

    }



    @Listener(order = Order.BEFORE_POST)
    public void onAttack(InteractEntityEvent.Primary event) {
        if (event.isCancelled())
            return;
        if (!Utils.isLivingEntity(event.getTargetEntity()))
            return;
        IEntity entity = entityService.get(event.getTargetEntity());
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            IActiveCharacter character = characterService.getCharacter(first.get().getUniqueId());
            if (character.isStub())
                return;
            //todo
            int activeHotbarslot = 8;
            character.getHotbar()[activeHotbarslot].onLeftClick(character);
        }
        if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter target = characterService.getCharacter(event.getTargetEntity().getUniqueId());
            if (target.isStub() && !PluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS) {
                event.setCancelled(true);
                return;
            }
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
    public void onRightClick(InteractEntityEvent.Secondary event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            Player pl = first.get();
            Optional<ItemStack> itemInHand = pl.getItemInHand();
            if (itemInHand.isPresent()) {
                ItemStack itemStack = itemInHand.get();
                IActiveCharacter character = characterService.getCharacter(pl.getUniqueId());
                if (character.isStub())
                    return;
                //todo
                int activeHotbarslot = 8;
                character.getHotbar()[activeHotbarslot].onRightClick(character);
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
    @Listener
    public void onChunkDespawn(UnloadChunkEvent event) {
        entityService.remove(event.getTargetChunk().getEntities(Utils::isLivingEntity));
    }
*/

    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
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
                        newdamage = entityService.getMobDamage(source.getType());
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
            Optional<IndirectEntityDamageSource> q = event.getCause().first(IndirectEntityDamageSource.class);
            if (q.isPresent()) {
                IndirectEntityDamageSource indirectEntityDamageSource = q.get();
                if (indirectEntityDamageSource.getSource() instanceof Projectile) {
                    Projectile projectile = (Projectile) indirectEntityDamageSource.getSource();
                    IEntity shooter = entityService.get((Entity)projectile.getShooter());
                    IEntity target = entityService.get(targetEntity);
                    ProjectileProperties projectileProperties = ProjectileProperties.cache.get(projectile);
                    if (projectileProperties != null) {
                        ProjectileProperties.cache.remove(projectile);
                        projectileProperties.consumer.accept(shooter,target);
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
