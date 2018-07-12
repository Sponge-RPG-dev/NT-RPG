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

package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 9.8.2015.
 */

public final class StartingPoint extends PassiveSkill {
	public static final Text name = Text.of("StartingPoint");
	public static final SkillData SKILL_DATA = new SkillData(name.toPlain());
	private static SkillSettings skillSettings = new SkillSettings();

	public StartingPoint() {
		super(null);
		setIcon(ItemTypes.WOOL);
	}

	@Override
	public boolean showsToPlayers() {
		return false;
	}

	@Override
	public String getName() {
		return name.toPlain();
	}

	@Override
	public List<Text> getDescription() {
		return Collections.emptyList();
	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {

	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {

	}

	@Override
	public void skillRefund(IActiveCharacter IActiveCharacter) {

	}

	@Override
	public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {

	}

	@Override
	public SkillSettings getDefaultSkillSettings() {
		return skillSettings;
	}

	@Override
	public void onCharacterInit(IActiveCharacter c, int level) {

	}

	@Override
	public SkillSettings getSettings() {
		return getSettings();
	}

	@Override
	public void setSettings(SkillSettings settings) {

	}


	@Override
	public Set<ISkillType> getSkillTypes() {
		return Collections.emptySet();
	}
}
