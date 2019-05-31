package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.api.events.character.*;
import cz.neumimto.rpg.api.events.damage.*;
import cz.neumimto.rpg.api.events.effect.*;
import cz.neumimto.rpg.api.events.party.*;
import cz.neumimto.rpg.api.events.skill.*;
import cz.neumimto.rpg.common.events.EventFactoryImpl;
import cz.neumimto.rpg.sponge.events.character.*;
import cz.neumimto.rpg.sponge.events.damage.*;
import cz.neumimto.rpg.sponge.events.effects.*;
import cz.neumimto.rpg.sponge.events.party.*;
import cz.neumimto.rpg.sponge.events.skill.SpongeHealEvent;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillPostUsageEvent;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillPreUsageEvent;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillTargetAttemptEvent;

public class SpongeEventFactory extends EventFactoryImpl {
    @Override
    public void registerEventProviders() {
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
    }
}
