package cz.neumimto.rpg.api.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandSkill extends ActiveSkill {

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        CommandData skillData = (CommandData) info.getSkillData();
        List<String> command = Collections.singletonList(skillData.getCommand());
        Map<String, String> args = new HashMap<>();
        args.put("{{player}}", character.getPlayerAccountName());

        if (skillData.isConsole()) {
            Rpg.get().executeCommandBatch(args, command);
        } else {
            Rpg.get().executeCommandAs(character.getUUID(), args, command);
        }

        skillContext.next(character, info, skillContext);
    }

    @Override
    public SkillData constructSkillData() {
        return new CommandData(getName());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        CommandData data = (CommandData) skillData;
        String command = c.getString("Command");
        data.command = command;
        try {
            boolean executeAsConsole = c.getBoolean("ExecuteAsConsole");
            data.console = executeAsConsole;
        } catch (ConfigException ignored) {}
    }

    public class CommandData extends SkillData {

        boolean console;

        private String command;

        public CommandData(String skill) {
            super(skill);
        }

        public boolean isConsole() {
            return console;
        }

        public String getCommand() {
            return command;
        }
    }
}
