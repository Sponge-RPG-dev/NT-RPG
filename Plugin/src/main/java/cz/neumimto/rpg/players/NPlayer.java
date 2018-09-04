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

package cz.neumimto.rpg.players;

import cz.neumimto.rpg.configuration.PluginConfig;

import java.util.Vector;

/**
 * Created by NeumimTo on 26.12.2014.
 */

public class NPlayer implements INPlayer {


	private final Vector<CharacterBase> characters = new Vector<>(PluginConfig.PLAYER_MAX_CHARS, 1);
	private IActiveCharacter activeIActiveCharacter;

	public IActiveCharacter getActiveActiveCharacter() {
		return activeIActiveCharacter;
	}

	public void setActiveActiveCharacter(IActiveCharacter c) {
		this.activeIActiveCharacter = c;
	}

	public Vector<CharacterBase> getCharacterList() {
		return characters;
	}

}
