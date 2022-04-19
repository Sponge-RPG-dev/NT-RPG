package cz.neumimto.rpg.spigot.events;

import cz.neumimto.rpg.common.events.EventFactoryImpl;
import cz.neumimto.rpg.common.events.character.*;
import cz.neumimto.rpg.common.events.damage.*;
import cz.neumimto.rpg.common.events.effect.EffectApplyEvent;
import cz.neumimto.rpg.common.events.effect.EffectRemoveEvent;
import cz.neumimto.rpg.common.events.party.PartyCreateEvent;
import cz.neumimto.rpg.common.events.party.PartyInviteEvent;
import cz.neumimto.rpg.common.events.party.PartyJoinEvent;
import cz.neumimto.rpg.common.events.party.PartyLeaveEvent;
import cz.neumimto.rpg.common.events.skill.*;
import cz.neumimto.rpg.spigot.events.character.*;
import cz.neumimto.rpg.spigot.events.damage.*;
import cz.neumimto.rpg.spigot.events.effects.SpigotEffectApplyEvent;
import cz.neumimto.rpg.spigot.events.effects.SpigotEffectRemoveEvent;
import cz.neumimto.rpg.spigot.events.party.SpigotPartyCreateEvent;
import cz.neumimto.rpg.spigot.events.party.SpigotPartyInviteEvent;
import cz.neumimto.rpg.spigot.events.party.SpigotPartyJoinEvent;
import cz.neumimto.rpg.spigot.events.party.SpigotPartyLeaveEvent;
import cz.neumimto.rpg.spigot.events.skill.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Singleton;

@Singleton
public class SpigotEventFactory extends EventFactoryImpl {

    @Override
    public Class listenerAnnotation() {
        return EventHandler.class;
    }

    @Override
    public Class listenerSubclass() {
        return Listener.class;
    }

    @Override
    public void registerEventProviders() {
        //by iface
        super.registerProvider(CharacterAttributeChange.class, SpigotCharacterAttributeChange::new);
        super.registerProvider(CharacterChangeGroupEvent.class, SpigotCharacterChangeGroupEvent::new);
        super.registerProvider(CharacterGainedLevelEvent.class, SpigotCharacterGainedLevelEvent::new);
        super.registerProvider(CharacterInitializedEvent.class, SpigotCharacterInitializedEvent::new);
        super.registerProvider(CharacterResourceChangeValueEvent.class, SpigotCharacterResourceChangeValueEvent::new);
        super.registerProvider(CharacterSkillLearnAttemptEvent.class, SpigotCharacterSkillLearnAttemptEvent::new);
        super.registerProvider(CharacterSkillRefundAttemptEvent.class, SpigotCharacterSkillRefundAttemptEvent::new);
        super.registerProvider(CharacterSkillUpgradeEvent.class, SpigotCharacterSkillUpgradeEvent::new);
        super.registerProvider(CharacterWeaponUpdateEvent.class, SpigotCharacterWeaponUpdateEvent::new);
        super.registerProvider(EventCharacterArmorPostUpdate.class, SpigotEventCharacterArmorPostUpdate::new);
        super.registerProvider(DamageIEntityEarlyEvent.class, SpigotDamageIEntityEarlyEvent::new);
        super.registerProvider(DamageIEntityLateEvent.class, SpigotDamageIEntityLateEvent::new);
        super.registerProvider(IEntitySkillDamageEarlyEvent.class, SpigotEntitySkillDamageEarlyEvent::new);
        super.registerProvider(IEntitySkillDamageLateEvent.class, SpigotEntitySkillDamageLateEvent::new);
        super.registerProvider(IEntityWeaponDamageEarlyEvent.class, SpigotEntityWeaponDamageEarlyEvent::new);
        super.registerProvider(IEntityWeaponDamageLateEvent.class, SpigotEntityWeaponDamageLateEvent::new);
        super.registerProvider(EffectApplyEvent.class, SpigotEffectApplyEvent::new);
        super.registerProvider(EffectRemoveEvent.class, SpigotEffectRemoveEvent::new);
        super.registerProvider(PartyCreateEvent.class, SpigotPartyCreateEvent::new);
        super.registerProvider(PartyInviteEvent.class, SpigotPartyInviteEvent::new);
        super.registerProvider(PartyJoinEvent.class, SpigotPartyJoinEvent::new);
        super.registerProvider(PartyLeaveEvent.class, SpigotPartyLeaveEvent::new);
        super.registerProvider(SkillHealEvent.class, SpigotHealEvent::new);
        super.registerProvider(SkillPostUsageEvent.class, SpigotSkillPostUsageEvent::new);
        super.registerProvider(SkillFinishedEvent.class, SpigotSkillFinishedEvent::new);
        super.registerProvider(SkillPreUsageEvent.class, SpigotSkillPreUsageEvent::new);
        super.registerProvider(SkillTargetAttemptEvent.class, SpigotSkillTargetAttemptEvent::new);
        //by impl
        super.registerProvider(SpigotCharacterAttributeChange.class, SpigotCharacterAttributeChange::new);
        super.registerProvider(SpigotCharacterChangeGroupEvent.class, SpigotCharacterChangeGroupEvent::new);
        super.registerProvider(SpigotCharacterGainedLevelEvent.class, SpigotCharacterGainedLevelEvent::new);
        super.registerProvider(SpigotCharacterInitializedEvent.class, SpigotCharacterInitializedEvent::new);
        super.registerProvider(SpigotCharacterResourceChangeValueEvent.class, SpigotCharacterResourceChangeValueEvent::new);
        super.registerProvider(SpigotCharacterSkillLearnAttemptEvent.class, SpigotCharacterSkillLearnAttemptEvent::new);
        super.registerProvider(SpigotCharacterSkillRefundAttemptEvent.class, SpigotCharacterSkillRefundAttemptEvent::new);
        super.registerProvider(SpigotCharacterSkillUpgradeEvent.class, SpigotCharacterSkillUpgradeEvent::new);
        super.registerProvider(SpigotCharacterWeaponUpdateEvent.class, SpigotCharacterWeaponUpdateEvent::new);
        super.registerProvider(SpigotEventCharacterArmorPostUpdate.class, SpigotEventCharacterArmorPostUpdate::new);
        super.registerProvider(SpigotDamageIEntityEarlyEvent.class, SpigotDamageIEntityEarlyEvent::new);
        super.registerProvider(SpigotDamageIEntityLateEvent.class, SpigotDamageIEntityLateEvent::new);
        super.registerProvider(SpigotEntitySkillDamageEarlyEvent.class, SpigotEntitySkillDamageEarlyEvent::new);
        super.registerProvider(SpigotEntitySkillDamageLateEvent.class, SpigotEntitySkillDamageLateEvent::new);
        super.registerProvider(SpigotEntityWeaponDamageEarlyEvent.class, SpigotEntityWeaponDamageEarlyEvent::new);
        super.registerProvider(SpigotEntityWeaponDamageLateEvent.class, SpigotEntityWeaponDamageLateEvent::new);
        super.registerProvider(SpigotEffectApplyEvent.class, SpigotEffectApplyEvent::new);
        super.registerProvider(SpigotEffectRemoveEvent.class, SpigotEffectRemoveEvent::new);
        super.registerProvider(SpigotPartyCreateEvent.class, SpigotPartyCreateEvent::new);
        super.registerProvider(SpigotPartyInviteEvent.class, SpigotPartyInviteEvent::new);
        super.registerProvider(SpigotPartyJoinEvent.class, SpigotPartyJoinEvent::new);
        super.registerProvider(SpigotPartyLeaveEvent.class, SpigotPartyLeaveEvent::new);
        super.registerProvider(SpigotHealEvent.class, SpigotHealEvent::new);
        super.registerProvider(SpigotSkillPostUsageEvent.class, SpigotSkillPostUsageEvent::new);
        super.registerProvider(SpigotSkillPreUsageEvent.class, SpigotSkillPreUsageEvent::new);
        super.registerProvider(SpigotSkillTargetAttemptEvent.class, SpigotSkillTargetAttemptEvent::new);
        super.registerProvider(SpigotEntityProjectileDamageLateEvent.class, SpigotEntityProjectileDamageLateEvent::new);
        super.registerProvider(SpigotEntityProjectileDamageEarlyEvent.class, SpigotEntityProjectileDamageEarlyEvent::new);

    }
}
