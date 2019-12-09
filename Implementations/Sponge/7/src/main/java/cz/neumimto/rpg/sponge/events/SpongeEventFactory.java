package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.api.events.character.*;
import cz.neumimto.rpg.api.events.damage.*;
import cz.neumimto.rpg.api.events.effect.EffectApplyEvent;
import cz.neumimto.rpg.api.events.effect.EffectRemoveEvent;
import cz.neumimto.rpg.api.events.party.*;
import cz.neumimto.rpg.api.events.skill.*;
import cz.neumimto.rpg.common.events.EventFactoryImpl;
import cz.neumimto.rpg.sponge.events.character.*;
import cz.neumimto.rpg.sponge.events.damage.*;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectApplyEvent;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectRemoveEvent;
import cz.neumimto.rpg.sponge.events.party.*;
import cz.neumimto.rpg.sponge.events.skill.*;

import javax.inject.Singleton;

@Singleton
public class SpongeEventFactory extends EventFactoryImpl {

    @Override
    public void registerEventProviders() {
        //by iface
        super.registerProvider(CharacterAttributeChange.class, SpongeCharacterAttributeChange::new);
        super.registerProvider(CharacterChangeGroupEvent.class, SpongeCharacterChangeGroupEvent::new);
        super.registerProvider(CharacterGainedLevelEvent.class, SpongeCharacterGainedLevelEvent::new);
        super.registerProvider(CharacterInitializedEvent.class, SpongeCharacterInitializedEvent::new);
        super.registerProvider(CharacterManaRegainEvent.class, SpongeCharacterManaRegainEvent::new);
        super.registerProvider(CharacterSkillLearnAttemptEvent.class, SpongeCharacterSkillLearnAttemptEvent::new);
        super.registerProvider(CharacterSkillRefundAttemptEvent.class, SpongeCharacterSkillRefundAttemptEvent::new);
        super.registerProvider(CharacterSkillUpgradeEvent.class, SpongeCharacterSkillUpgradeEvent::new);
        super.registerProvider(CharacterWeaponUpdateEvent.class, SpongeCharacterWeaponUpdateEvent::new);
        super.registerProvider(EventCharacterArmorPostUpdate.class, SpongeEventCharacterArmorPostUpdate::new);
        super.registerProvider(DamageIEntityEarlyEvent.class, SpongeDamageIEntityEarlyEvent::new);
        super.registerProvider(DamageIEntityLateEvent.class, SpongeDamageIEntityLateEvent::new);
        super.registerProvider(IEntitySkillDamageEarlyEvent.class, SpongeEntitySkillDamageEarlyEvent::new);
        super.registerProvider(IEntitySkillDamageLateEvent.class, SpongeEntitySkillDamageLateEvent::new);
        super.registerProvider(IEntityWeaponDamageEarlyEvent.class, SpongeEntityWeaponDamageEarlyEvent::new);
        super.registerProvider(IEntityWeaponDamageLateEvent.class, SpongeEntityWeaponDamageLateEvent::new);
        super.registerProvider(EffectApplyEvent.class, SpongeEffectApplyEvent::new);
        super.registerProvider(EffectRemoveEvent.class, SpongeEffectRemoveEvent::new);
        super.registerProvider(PartyCreateEvent.class, SpongePartyCreateEvent::new);
        super.registerProvider(PartyInviteEvent.class, SpongePartyInviteEvent::new);
        super.registerProvider(PartyJoinEvent.class, SpongePartyJoinEvent::new);
        super.registerProvider(PartyLeaveEvent.class, SpongePartyLeaveEvent::new);
        super.registerProvider(SkillHealEvent.class, SpongeHealEvent::new);
        super.registerProvider(SkillPostUsageEvent.class, SpongeSkillPostUsageEvent::new);
        super.registerProvider(SkillPreUsageEvent.class, SpongeSkillPreUsageEvent::new);
        super.registerProvider(SkillTargetAttemptEvent.class, SpongeSkillTargetAttemptEvent::new);
        //by impl
        super.registerProvider(SpongeCharacterAttributeChange.class, SpongeCharacterAttributeChange::new);
        super.registerProvider(SpongeCharacterChangeGroupEvent.class, SpongeCharacterChangeGroupEvent::new);
        super.registerProvider(SpongeCharacterGainedLevelEvent.class, SpongeCharacterGainedLevelEvent::new);
        super.registerProvider(SpongeCharacterInitializedEvent.class, SpongeCharacterInitializedEvent::new);
        super.registerProvider(SpongeCharacterManaRegainEvent.class, SpongeCharacterManaRegainEvent::new);
        super.registerProvider(SpongeCharacterSkillLearnAttemptEvent.class, SpongeCharacterSkillLearnAttemptEvent::new);
        super.registerProvider(SpongeCharacterSkillRefundAttemptEvent.class, SpongeCharacterSkillRefundAttemptEvent::new);
        super.registerProvider(SpongeCharacterSkillUpgradeEvent.class, SpongeCharacterSkillUpgradeEvent::new);
        super.registerProvider(SpongeCharacterWeaponUpdateEvent.class, SpongeCharacterWeaponUpdateEvent::new);
        super.registerProvider(SpongeEventCharacterArmorPostUpdate.class, SpongeEventCharacterArmorPostUpdate::new);
        super.registerProvider(SpongeDamageIEntityEarlyEvent.class, SpongeDamageIEntityEarlyEvent::new);
        super.registerProvider(SpongeDamageIEntityLateEvent.class, SpongeDamageIEntityLateEvent::new);
        super.registerProvider(SpongeEntitySkillDamageEarlyEvent.class, SpongeEntitySkillDamageEarlyEvent::new);
        super.registerProvider(SpongeEntitySkillDamageLateEvent.class, SpongeEntitySkillDamageLateEvent::new);
        super.registerProvider(SpongeEntityWeaponDamageEarlyEvent.class, SpongeEntityWeaponDamageEarlyEvent::new);
        super.registerProvider(SpongeEntityWeaponDamageLateEvent.class, SpongeEntityWeaponDamageLateEvent::new);
        super.registerProvider(SpongeEffectApplyEvent.class, SpongeEffectApplyEvent::new);
        super.registerProvider(SpongeEffectRemoveEvent.class, SpongeEffectRemoveEvent::new);
        super.registerProvider(SpongePartyCreateEvent.class, SpongePartyCreateEvent::new);
        super.registerProvider(SpongePartyInviteEvent.class, SpongePartyInviteEvent::new);
        super.registerProvider(SpongePartyJoinEvent.class, SpongePartyJoinEvent::new);
        super.registerProvider(SpongePartyLeaveEvent.class, SpongePartyLeaveEvent::new);
        super.registerProvider(SpongeHealEvent.class, SpongeHealEvent::new);
        super.registerProvider(SpongeSkillPostUsageEvent.class, SpongeSkillPostUsageEvent::new);
        super.registerProvider(SpongeSkillPreUsageEvent.class, SpongeSkillPreUsageEvent::new);
        super.registerProvider(SpongeSkillTargetAttemptEvent.class, SpongeSkillTargetAttemptEvent::new);
        super.registerProvider(SpongeEntityProjectileDamageLateEvent.class, SpongeEntityProjectileDamageLateEvent::new);
        super.registerProvider(SpongeEntityProjectileDamageEarlyEvent.class, SpongeEntityProjectileDamageEarlyEvent::new);
    }
}
