package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.persistance.SkillPathData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

/**
 * Created by NeumimTo on 16.8.17.
 */

public class SkillTreePath extends PassiveSkill {


	public SkillTreePath(String name) {
		super(null);
		setName(name);
		SkillSettings settings = new SkillSettings();
		addSkillType(SkillType.PATH);
		super.setSettings(settings);
	}

	@Override
	public SkillResult onPreUse(IActiveCharacter character) {
		return SkillResult.CANCELLED;
	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {
		if (PluginConfig.PLAYER_CHOOSED_SKILLTREE_PATH_GLOBAL_MESSAGE) {
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
	}

	@Override
	public void skillRefund(IActiveCharacter c) {
		if (PluginConfig.PATH_NODES_SEALED) {
			//todo
		} else {
			ExtendedSkillInfo skillInfo = c.getSkillInfo(this);
			SkillData skillData = skillInfo.getSkillData();
			SkillPathData pdata = (SkillPathData) skillData;
			SubjectData transientSubjectData = c.getPlayer().getTransientSubjectData();
			for (String perm : pdata.getPermissions()) {
				transientSubjectData.setPermission(SubjectData.GLOBAL_CONTEXT, perm, Tristate.FALSE);
			}
		}
	}
}
