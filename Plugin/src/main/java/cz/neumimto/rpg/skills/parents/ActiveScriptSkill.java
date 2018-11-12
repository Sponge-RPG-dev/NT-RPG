package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkillType;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.scripting.ScriptExecutorSkill;
import cz.neumimto.rpg.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.skills.mods.SkillContext;

import java.util.List;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class ActiveScriptSkill extends ActiveSkill implements ScriptSkill<ScriptExecutorSkill> {

	private ScriptExecutorSkill executor;

	private ScriptSkillModel model;

	@Override
	public void cast(IActiveCharacter character, ExtendedSkillInfo info, SkillContext chain) {
		SkillScriptContext context = new SkillScriptContext(this, info);
		executor.cast(character, info, chain, context);
		SkillResult skillResult = context.getResult() == null ? SkillResult.OK : context.getResult();
		chain.next(character, info, chain.result(skillResult));
	}

	@Override
	public void setExecutor(ScriptExecutorSkill ses) {
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
		List<ISkillType> skillTypes = model.getSkillTypes();
		skillTypes.forEach(super::addSkillType);
	}

	@Override
	public String getTemplateName() {
		return "templates/active.js";
	}
}
