package cz.neumimto.commands;

import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.*;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by NeumimTo on 28.7.2015.
 */
@Command
public class CommandAdmin extends CommandBase {

    @Inject
    private SkillService skillService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Logger logger;

    public CommandAdmin() {
        setDescription("Bypass many plugin restrictions, allows you to force execute skill, set character properties..., bad use of this command may breaks plugin mechanics or cause exceptions.");
        setPermission("ntrpg.superadmin");
        setUsage("nadmin");
        addAlias("nadmin");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] a = s.split(" ");
        if (a[0].equalsIgnoreCase("use")) {
            if (!(commandSource instanceof Player)) {
                logger.debug("Can't be executed from console");
                return CommandResult.empty();
            }
            if (a[1].equalsIgnoreCase("skill")) {
                ISkill skill = skillService.getSkill(a[2]);
                SkillSettings defaultSkillSettings = skill.getDefaultSkillSettings();
                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
                if (character.isStub())
                    throw new RuntimeException("Character is required even for admin.");
                int level = 1;
                if (a.length == 4)
                    level = Integer.parseInt(a[3]);
                if (skill instanceof ActiveSkill) {
                    ExtendedSkillInfo extendedSkillInfo = new ExtendedSkillInfo();
                    extendedSkillInfo.setLevel(level);
                    SkillInfo skillInfo = new SkillInfo(skill.getName());
                    skillInfo.setSkillSettings(defaultSkillSettings);
                    extendedSkillInfo.setSkillInfo(skillInfo);
                    extendedSkillInfo.setSkill(skill);
                    ActiveSkill askill = (ActiveSkill) skill;
                    long start = System.nanoTime();
                    askill.cast(character, extendedSkillInfo);
                    long end = System.nanoTime();
                    character.sendMessage("Executing the skill took: " + (end-start) + " ns");
                }
            }
        } else if (a[0].equalsIgnoreCase("set")) {

        }
        return CommandResult.success();
    }
}
