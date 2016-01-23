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

package cz.neumimto.players.groups;

import cz.neumimto.skills.ISkill;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class Race extends PlayerGroup {

    public static Race Default = new Race("None");
    private List<ISkill> skills = new ArrayList<>();
    private Set<NClass> allowedRaces;

    public Race(String name) {
        super(name);
    }

    public List<ISkill> getSkills() {
        return skills;
    }


    public void setAllowedClasses(Set<NClass> allowedRaces) {
        this.allowedRaces = allowedRaces;
    }

    public Set<NClass> getAllowedClasses() {
        return allowedRaces;
    }
}
