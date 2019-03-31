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

package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.events.skill.SkillEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 26.7.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class CharacterSkillLearnAttemptEvent extends AbstractCharacterCancellableEvent implements SkillEvent {
	private final ISkill skill;
	private Text failedMessage;

	public CharacterSkillLearnAttemptEvent(IActiveCharacter character, ISkill skill) {
		super(character);
		this.skill = skill;
	}

	@Override
	public ISkill getSkill() {
		return skill;
	}

	public Text getFailedMessage() {
		return failedMessage == null ? Text.EMPTY : failedMessage;
	}

	public void setFailedMessage(Text failedMessage) {
		this.failedMessage = failedMessage;
	}
}
