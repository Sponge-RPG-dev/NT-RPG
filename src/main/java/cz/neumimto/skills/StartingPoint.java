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

package cz.neumimto.skills;

import cz.neumimto.players.IActiveCharacter;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 9.8.2015.
 */

public class StartingPoint extends PassiveSkill {
    public static String name = "StartingPoint";
    private static SkillSettings skillSettings = new SkillSettings();
    private static String desc = "";

    @Override
    public boolean showsToPlayers() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

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
    public SkillSettings getDefaultSkillSettings() {
        return skillSettings;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {

    }

    @Override
    public void init() {

    }

    @Override
    public SkillSettings getSettings() {
        return getDefaultSkillSettings();
    }

    @Override
    public void setSettings(SkillSettings settings) {

    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public Set<SkillType> getSkillTypes() {
        return Collections.EMPTY_SET;
    }
}
