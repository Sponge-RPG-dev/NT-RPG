package cz.neumimto.rpg.api.skills.tree;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillPathData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 16.8.17.
 */

public class SkillTreeSpecialization extends PassiveSkill {

    private static final String PLAYER = "player";

    public SkillTreeSpecialization() {
        super();
        SkillSettings settings = new SkillSettings();
        super.setSettings(settings);
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_CHOOSED_SKILLTREE_SPECIALIZATION_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_CHOOSED_SKILLTREE_PATH_GLOBAL_MESSAGE_CONTENT,
                    Arg.arg(PLAYER, IActiveCharacter.getName())
                            .with("character", IActiveCharacter.getName())
                            .with("path", context.getSkillData().getSkillName()));

        }
        onCharacterInit(IActiveCharacter, 1, context);
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {

    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        PlayerSkillContext skillInfo = c.getSkillInfo(this);
        SkillData skillData = skillInfo.getSkillData();
        SkillPathData pdata = (SkillPathData) skillData;

        if (pdata.getEnterCommands() != null) {
            Map<String, String> args = new HashMap<>();
            args.put(PLAYER, c.getName());
            Rpg.get().executeCommandBatch(args, pdata.getEnterCommands());
        }

        for (Map.Entry<String, Integer> entry : pdata.getSkillBonus().entrySet()) {
            PlayerSkillContext skill = c.getSkill(entry.getKey());
            skill.setBonusLevel(skill.getBonusLevel() + entry.getValue());
        }

    }

    @Override
    public void skillRefund(IActiveCharacter c, PlayerSkillContext context) {
        SkillData skillData = context.getSkillData();
        SkillPathData pdata = (SkillPathData) skillData;

        if (pdata.getEnterCommands() != null) {
            Map<String, String> args = new HashMap<>();
            args.put(PLAYER, c.getName());
            Rpg.get().executeCommandBatch(args, pdata.getExitCommands());
        }
    }


    @Override
    public SkillPathData constructSkillData() {
        return new SkillPathData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree skillTree, SkillLoadingErrors logger, Config c) {
        SkillPathData pdata = (SkillPathData) skillData;
        try {
            List<String> ec = c.getStringList("EnterCommands");
            pdata.getEnterCommands().addAll(ec);
        } catch (ConfigException e) {

        }
        try {
            List<String> ec = c.getStringList("ExitCommands");
            pdata.getExitCommands().addAll(ec);
        } catch (ConfigException e) {

        }

        try {
            int tier = c.getInt("Tier");
            pdata.setTier(tier);
        } catch (ConfigException e) {
            logger.log("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no tier defined, setting to 0");
        }

        try {
            pdata.setSkillPointsRequired(c.getInt("SkillPointsRequired"));
        } catch (ConfigException e) {
            logger.log("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined, setting to 1");
            pdata.setSkillPointsRequired(1);
        }

        try {
            List<? extends Config> skillBonus = c.getConfigList("SkillBonus");
            for (Config s : skillBonus) {
                try {
                    String skill = s.getString("Skill");
                    int levels = s.getInt("Levels");
                    pdata.addSkillBonus(skill, levels);
                } catch (ConfigException e) {
                    logger.log(
                            "Found SkillPath.SkillBonus in the tree \"" + skillTree.getId() + "\" missing \"skill\" or \"level\" configuration "
                                    + "node");
                }

            }
        } catch (ConfigException e) {
        }

        pdata.setCombination(null);
        pdata.setMaxSkillLevel(1);
    }
}
