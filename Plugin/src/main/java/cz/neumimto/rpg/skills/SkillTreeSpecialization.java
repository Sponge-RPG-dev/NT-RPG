package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.jboss.logging.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

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

        SubjectData transientSubjectData = c.getPlayer().getTransientSubjectData();
        for (String perm : pdata.getPermissions()) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.TRUE);
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
        SubjectData transientSubjectData = c.getPlayer().getTransientSubjectData();
        for (String perm : pdata.getPermissions()) {
            transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.FALSE);
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
            List<String> permissions = c.getStringList("permissions");
            pdata.getPermissions().addAll(permissions);
        } catch (ConfigException e) {
            logger.log("Found SkillPath in the tree \"" + skillTree.getId() + "\" but no permissions defined");
        }
        try {
            int tier = c.getInt("tier");
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
                    String skill = s.getString("skill");
                    int levels = s.getInt("levels");
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
