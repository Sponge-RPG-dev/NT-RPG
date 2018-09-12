package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.skills.scripting.TargettedScriptExecutorSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.utils.SkillModifier;
import org.spongepowered.api.entity.living.Living;

import java.util.List;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargettedScriptSkill extends Targetted implements ITargettedScriptSkill {

	private TargettedScriptExecutorSkill executor;

	private ScriptSkillModel model;

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info, SkillModifier modifier) {
		SkillScriptContext context = new SkillScriptContext(this, info);
		return executor.cast(source, NtRpgPlugin.GlobalScope.entityService.get(target), modifier, context);
	}

	public void setExecutor(TargettedScriptExecutorSkill ses) {
		this.executor = ses;
	}

	@Override
	public ScriptSkillModel getModel() {
		return model;
	}

	public void setModel(ScriptSkillModel model) {
		this.model = model;
		setLore(model.getLore());
		setDamageType(model.getDamageType());
		setDescription(model.getDescription());
		setLocalizableName(model.getName());
		List<SkillType> skillTypes = model.getSkillTypes();
		skillTypes.forEach(super::addSkillType);
	}


}
