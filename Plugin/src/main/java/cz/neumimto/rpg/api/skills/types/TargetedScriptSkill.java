package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ITargetedScriptSkill;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkillType;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import cz.neumimto.rpg.skills.scripting.SkillScriptContext;
import cz.neumimto.rpg.skills.scripting.TargetedScriptExecutorSkill;

import java.util.List;

/**
 * Created by NeumimTo on 3.9.2018.
 */
public class TargetedScriptSkill extends Targeted implements ITargetedScriptSkill {

	private TargetedScriptExecutorSkill executor;

	private ScriptSkillModel model;

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		SkillScriptContext context = new SkillScriptContext(this, info);
		executor.cast(source, target, skillContext, context);
		context.setResult(context.getResult() == null ? SkillResult.OK : context.getResult());
		skillContext.next(source,info, context.getResult());
	}

	@Override
	public void setExecutor(TargetedScriptExecutorSkill ses) {
		this.executor = ses;
	}

	@Override
	public ScriptSkillModel getModel() {
		return model;
	}

	@Override
	public void setModel(ScriptSkillModel model) {
		this.model = model;
		setLore(model.getLore());
		setDamageType(model.getDamageType());
		setDescription(model.getDescription());
		setLocalizableName(model.getName());
		List<ISkillType> skillTypes = model.getSkillTypes();
		skillTypes.forEach(super::addSkillType);
	}


}
