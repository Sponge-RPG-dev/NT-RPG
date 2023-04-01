package cz.neumimto.rpg.common.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandSkill extends ActiveSkill<ActiveCharacter> {

    @Inject
    private PermissionService permissionService;

    @Override
    public SkillResult cast(ActiveCharacter character, PlayerSkillContext info) {
        CommandData skillData = (CommandData) info.getSkillData();
        List<String> command = Collections.singletonList(skillData.getCommand());
        Map<String, String> args = new HashMap<>();
        args.put("player", character.getPlayerAccountName());

        if (skillData.isConsole()) {
            Rpg.get().executeCommandBatch(args, command);
        } else {
            boolean permApplied = false;

            if (skillData.permission != null && permissionService.hasPermission(character, skillData.permission)) {
                permissionService.addPermissions(character, Collections.singletonList(skillData.permission));
                permApplied = true;
            }
            try {
                Rpg.get().executeCommandAs(character.getUUID(), args, command);
            } finally {
                if (permApplied) {
                    permissionService.removePermissions(character, Collections.singletonList(skillData.permission));
                }
            }
        }

        return SkillResult.OK;
    }

    @Override
    public SkillData constructSkillData() {
        return new CommandData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        CommandData data = (CommandData) skillData;
        String command = c.getString("Command");
        data.command = command;
        try {
            boolean executeAsConsole = c.getBoolean("ExecuteAsConsole");
            data.console = executeAsConsole;
        } catch (ConfigException ignored) {
        }

        try {
            String perm = c.getString("Permission");
            data.permission = perm;
        } catch (ConfigException ignored) {
        }
    }

    public class CommandData extends SkillData {

        public String permission;
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
