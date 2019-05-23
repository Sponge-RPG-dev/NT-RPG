/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.mods.SkillContext;

import javax.inject.Inject;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class PassiveSkill extends AbstractSkill {

	@Inject
	protected EffectService effectService;

	protected String relevantEffectName;

	public PassiveSkill() {
	}

	public PassiveSkill(String name) {
		this.relevantEffectName = name;
	}

	@Override
	public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
		character.sendMessage(Localizations.CANT_USE_PASSIVE_SKILL);
		skillContext.result(SkillResult.CANCELLED);
	}

	private void update(IActiveCharacter IActiveCharacter) {
		NtRpgPlugin.GlobalScope.inventorySerivce.initializeCharacterInventory(IActiveCharacter);
		PlayerSkillContext skill = IActiveCharacter.getSkill(getId());
		applyEffect(skill, IActiveCharacter);
	}

	@Override
	public void onCharacterInit(IActiveCharacter c, int level) {
		super.onCharacterInit(c, level);
		update(c);
	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {
		super.skillLearn(IActiveCharacter);
		update(IActiveCharacter);
	}

	@Override
	public void skillRefund(IActiveCharacter IActiveCharacter) {
		super.skillRefund(IActiveCharacter);
		PlayerSkillContext skillInfo = IActiveCharacter.getSkillInfo(this);
		if (skillInfo.getLevel() <= 0) {
			effectService.removeEffect(relevantEffectName, IActiveCharacter, this);
		} else {
			update(IActiveCharacter);
		}
	}

	public abstract void applyEffect(PlayerSkillContext info, IActiveCharacter character);
}
