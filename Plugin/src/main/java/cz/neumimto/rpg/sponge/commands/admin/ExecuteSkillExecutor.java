package cz.neumimto.rpg.sponge.commands.admin;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.IActiveSkill;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.players.CommandblockSkillExecutor;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ExecuteSkillExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ISkill skill = args.<ISkill>getOne("skill").get();

        IActiveCharacter character;
        SkillSettings defaultSkillSettings;
        if (!(src instanceof Player)) {
            Location location = null;
            if (src instanceof CommandBlockSource) {
                CommandBlockSource source = (CommandBlockSource) src;
                Optional<Object> olocation = args.getOne("loc");
                location = source.getLocation();
                if (olocation.isPresent()) {
                    String o = (String) olocation.get();
                    location = Utils.getLocationRelative(o, location);
                }
            } else {
                Optional<String> olocation = args.getOne("loc");
                if (!olocation.isPresent()) {
                    throw new CommandException(Text.of("Loc flag needs to be present"));
                }
                location = Utils.getLocationRelative(olocation.get());
            }

            Optional<String> head = args.getOne("head");
            Vector3d headRotation = head.map(s -> {
                String[] split = s.split(";");
                return new Vector3d(
                        Double.parseDouble(Utils.extractNumber(split[0])),
                        Double.parseDouble(Utils.extractNumber(split[1])),
                        Double.parseDouble(Utils.extractNumber(split[2]))
                );
            }).orElse(new Vector3d());
            character = CommandblockSkillExecutor.wrap(location, headRotation);

            defaultSkillSettings = args.<String>getOne("settings").map(o -> {
                SkillSettings skillSettings = new SkillSettings();
                String[] split = o.split(";");
                for (String s : split) {
                    String[] w = s.split(":");
                    skillSettings.addNode(w[0], Float.parseFloat(w[1]), 0f);
                }
                return skillSettings;
            }).orElse(skill.getDefaultSkillSettings());


        } else {
            defaultSkillSettings = skill.getSettings();
            Player player = (Player) src;
            character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                throw new RuntimeException("Character is required even for an admin.");
            }
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
