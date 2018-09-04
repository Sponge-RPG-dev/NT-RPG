package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.scripting.ScriptExecutorSkill;
import cz.neumimto.rpg.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.utils.SkillModifier;

import java.util.List;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class ActiveScriptSkill extends ActiveSkill implements ScriptSkill<ScriptExecutorSkill> {

	private ScriptExecutorSkill executor;

	private ScriptSkillModel model;

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		SkillScriptContext context = new SkillScriptContext(this, info);
		SkillResult cast = executor.cast(character, info, modifier, context);
		return cast;
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
		List<SkillType> skillTypes = model.getSkillTypes();
		skillTypes.forEach(super::addSkillType);
	}

	@Override
	public String getTemplateName() {
		return "templates/active.js";
	}
}
