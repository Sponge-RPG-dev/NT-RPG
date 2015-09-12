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

package cz.neumimto.players;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 17.3.2015.
 */
public class CharacterAttributes {
    private Map<Class<? extends CharacterAttribute>, CharacterAttribute> attributes = new HashMap<>();
    public static Set<CharacterAttributeBuilder> builders = new HashSet<>();
    private IActiveCharacter IActiveCharacter;

    public CharacterAttributes(IActiveCharacter IActiveCharacter) {
        for (CharacterAttributeBuilder b : builders) {

        }
    }


    public void changeValue(Class<? extends CharacterAttribute> c, int amount) {
        CharacterAttribute a = attributes.get(c);

    }
}
