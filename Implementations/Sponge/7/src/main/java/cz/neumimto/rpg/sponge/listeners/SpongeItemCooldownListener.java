package cz.neumimto.rpg.sponge.listeners;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillFinishedEvent;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillPostUsageEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.item.ItemType;

import java.util.Optional;

public class SpongeItemCooldownListener {


    @Listener
    public void onSkillPostUsageEvent(SpongeSkillFinishedEvent event) {
        SkillContext skillContext = event.getSkillContext();
        int cd = (int) skillContext.getFinalCooldown();
        if (cd > 0) {
            IEntity caster = event.getCaster();
            ISkill skill = event.getSkill();

            if (caster instanceof ISpongeCharacter) {
                ISpongeCharacter character = (ISpongeCharacter) caster;
                Player player = character.getPlayer();

                PlayerSkillContext skillInfo = character.getSkillInfo(skill);
                String icon = skillInfo.getSkillData().getIcon();

                if (icon != null) {
                    cd /= 50;
                    Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, icon);
                    if (type.isPresent()) {
                        ItemType itemType = type.get();
                        player.getCooldownTracker().setCooldown(itemType, cd);
                    }
                }
            }
        }
    }
}
