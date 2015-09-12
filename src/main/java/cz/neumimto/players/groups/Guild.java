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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class Guild extends PlayerGroup {
    public static Guild Default = new Guild("None");

    private Set<ISkill> skills = new HashSet<>();

    public Guild(String name) {
        super(name);
    }


}
