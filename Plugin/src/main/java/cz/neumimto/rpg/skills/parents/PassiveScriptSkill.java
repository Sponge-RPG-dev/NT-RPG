package cz.neumimto.rpg.skills.parents;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkillType;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import cz.neumimto.rpg.skills.scripting.PassiveScriptSkillHandler;
import cz.neumimto.rpg.skills.scripting.SkillScriptContext;

import java.util.List;

/**
 * Created by NeumimTo on 7.10.2018.
 */
public class PassiveScriptSkill extends PassiveSkill implements IPassiveScriptSkill {


	private PassiveScriptSkillHandler handler;

	private ScriptSkillModel model;

	@Override
	public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
		handler.init(character, info, new SkillScriptContext(this, info));
	}

	@Override
	public void setExecutor(PassiveScriptSkillHandler ses) {
		this.handler = ses;
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
		setLocalizableName(TextHelper.parse(model.getName()));
		List<ISkillType> skillTypes = model.getSkillTypes();
		skillTypes.forEach(super::addSkillType);
	}
}
