package cz.neumimto.rpg.spigot.listeners;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.common.skills.preprocessors.SkillCostPreprocessor;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.events.skill.SpigotSkillPostUsageEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpigotItemCooldownListener implements Listener {

    @EventHandler
    public void onSkillPostUsageEvent(SpigotSkillPostUsageEvent event) {
        SkillContext skillContext = event.getSkillContext();
        int cd = (int) skillContext.getFinalCooldown();
        if (cd > 0) {
            IEntity caster = event.getCaster();
            ISkill skill = event.getSkill();

            if (caster instanceof ISpigotCharacter) {
                ISpigotCharacter character = (ISpigotCharacter) caster;
                Player player = character.getPlayer();

                PlayerSkillContext skillInfo = character.getSkillInfo(skill);
                String icon = skillInfo.getSkillData().getIcon();

                if (icon != null) {
                    cd /= 50;
                    Material material = Material.matchMaterial(icon);
                    player.setCooldown(material, cd);
                }
            }
        }
    }

}
