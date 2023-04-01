package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("bind")
@CommandPermission("ntrpg.player.skillbind")
public class SpigotSkillBindCommands extends BaseCommand {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private SpigotInventoryService inventoryService;

    @Default
    @CommandCompletion("@learnedskill")
    public void bindSkillCommand(Player executor, ISkill skill) {
        if (!(skill instanceof ActiveSkill)) {
            String msg = localizationService.translate(LocalizationKeys.CANNOT_BIND_NON_EXECUTABLE_SKILL);
            executor.sendMessage(msg);
            return;
        }
        if (executor.getMainHand() != null) {
            ActiveCharacter character = characterService.getCharacter(executor);
            if (character.isStub()) {
                return;
            }
            PlayerSkillContext info = character.getSkillInfo(skill);
            ItemStack is = inventoryService.createSkillbind(info.getSkillData());
            if (executor.getItemInHand().getType() == Material.AIR) {
                executor.setItemInHand(is);
            }
        }
    }
}
