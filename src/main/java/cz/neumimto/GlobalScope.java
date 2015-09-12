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

package cz.neumimto;

import cz.neumimto.effects.EffectService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.properties.PlayerPropertyService;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.Game;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class GlobalScope {

    @Inject
    public CharacterService characterService;

    @Inject
    public EffectService effectService;

    @Inject
    public GroupService groupService;

    @Inject
    public PlayerPropertyService playerPropertyService;

    @Inject
    public SkillService skillService;

    @Inject
    public NtRpgPlugin plugin;

    @Inject
    public Game game;

}
