package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.jboss.logging.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 16.8.17.
 */

public class SkillTreeSpecialization extends PassiveSkill {

    public static Logger logger = Logger.getLogger("SkillTreeSpecialization");

    public SkillTreeSpecialization(String name) {
        super(null);
        setName(name);
        SkillSettings settings = new SkillSettings();
        addSkillType(SkillType.PATH);
        setIcon(ItemTypes.BOOK);
        super.setSettings(settings);
    }

    @Override
    public SkillResult onPreUse(IActiveCharacter character) {
        return SkillResult.CANCELLED;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {
        if (PluginConfig.PLAYER_CHOOSED_SKILLTREE_SPECIALIZATIon_GLOBAL_MESSAGE) {
            Text t = Text.of(Localization.PLAYER_CHOOSED_SKILLTREE_PATH_GLOBAL_MESSAGE_CONTENT.replace("%1", IActiveCharacter.getName()).replace("%2", getName()));
            game.getServer().getOnlinePlayers().forEach(p -> p.sendMessage(t));
        }
    }

    @Override
    public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {

    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {
        super.onCharacterInit(c, level);
        ExtendedSkillInfo skillInfo = c.getSkillInfo(this);
        SkillData skillData = skillInfo.getSkillData();
        SkillPathData pdata = (SkillPathData) skillData;

        if (pdata.getEnterCommands() != null) {
            Map<String, String> args = new HashMap<>();
            args.put("player", c.getPlayer().getName());
            args.put("uuid", c.getPlayer().getUniqueId().toString());
            Utils.executeCommandBatch(args, pdata.getEnterCommands());
        }

        for (Map.Entry<String, Integer> entry : pdata.getSkillBonus().entrySet()) {
            ExtendedSkillInfo skill = c.getSkill(entry.getKey());
            skill.setBonusLevel(skill.getBonusLevel() + entry.getValue());
        }

    }

    @Override
    public void skillRefund(IActiveCharacter c) {
        ExtendedSkillInfo skillInfo = c.getSkillInfo(this);
        SkillData skillData = skillInfo.getSkillData();
        SkillPathData pdata = (SkillPathData) skillData;

        if (pdata.getEnterCommands() != null) {
            Map<String, String> args = new HashMap<>();
            args.put("player", c.getPlayer().getName());
            args.put("uuid", c.getPlayer().getUniqueId().toString());
            Utils.executeCommandBatch(args, pdata.getExitCommands());
        }
    }


    @Override
    public SkillPathData constructSkillData() {
        return new SkillPathData(getName());
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
        pdata.setMaxSkillLevel(1);
        try {
            List<? extends Config> skillBonus = c.getConfigList("SkillBonus");
            for (Config s : skillBonus) {
                try {
                    String skill = s.getString("Skill");
                    int levels = s.getInt("Levels");
                    pdata.addSkillBonus(skill, levels);
                } catch (ConfigException e) {
                    logger.log("Found SkillPath.SkillBonus in the tree \"" + skillTree.getId() + "\" missing \"skill\" or \"level\" configuration node");
                }

            }
        } catch (ConfigException e) {
            //logger.info("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined, setting to 1");
        }
        try {
            String a = c.getString("ItemIcon");
            Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, a);
            type.ifPresent(this::setIcon);
        } catch (ConfigException e) {

        }

        pdata.setCombination(null);
        pdata.setMaxSkillLevel(1);
    }
}
