package cz.neumimto.rpg.commands.admin;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.parents.IActiveSkill;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ExecuteSkillExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		ISkill skill = args.<ISkill>getOne("skill").get();
		SkillSettings defaultSkillSettings = skill.getSettings();
		Player player = (Player) src;
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
		if (character.isStub()) {
			throw new RuntimeException("Character is required even for an admin.");
		}

		int level = 1;
		Optional<Integer> optional = args.getOne("level");
		if (optional.isPresent()) {
			level = optional.get();
		}
		if (skill instanceof ActiveSkill) {
			Long l = System.nanoTime();

			PlayerSkillContext playerSkillContext = new PlayerSkillContext(null, skill, character);
			playerSkillContext.setLevel(level);
			SkillData skillData = new SkillData(skill.getId());
			skillData.setSkillSettings(defaultSkillSettings);
			playerSkillContext.setSkillData(skillData);
			playerSkillContext.setSkill(skill);

			SkillContext skillContext = new SkillContext((IActiveSkill) skill, playerSkillContext) {{
				wrappers.add(new SkillExecutorCallback() {
					@Override
					public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
						Long e = System.nanoTime();
						character.getPlayer().sendMessage(Text.of("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - l, TimeUnit.NANOSECONDS)));
					}
				});
			}};

			skillContext.sort();
			skillContext.next(character, playerSkillContext, skillContext);
		}
		return CommandResult.success();
	}
}
