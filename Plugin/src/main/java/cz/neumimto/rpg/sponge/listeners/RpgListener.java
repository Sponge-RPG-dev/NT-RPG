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

package cz.neumimto.rpg.sponge.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.events.PlayerGuiModInitEvent;
import cz.neumimto.rpg.events.character.CharacterChangeGroupEvent;
import cz.neumimto.rpg.events.party.PartyJoinEvent;
import cz.neumimto.rpg.players.CharacterService;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.inject.Inject;
import java.util.UUID;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;


/**
 * Created by NeumimTo on 12.2.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class RpgListener {

	@Inject
	private CharacterService characterService;

	@Inject
	private ClassService classService;

	@Listener
	public void onGuiInit(PlayerGuiModInitEvent event) {
		UUID uuid = event.getUuid();
		characterService.getCharacter(uuid).setUsingGuiMod(true);
	}


	@Listener(order = Order.EARLY)
	public void onPartyJoin(PartyJoinEvent event) {
		if (pluginConfig.MAX_PARTY_SIZE > -1) {
			if (event.getParty().getPlayers().size() > pluginConfig.MAX_PARTY_SIZE) {
				event.setCancelled(true);
			}
		}
	}

	@Listener
	public void onChangeGroup(CharacterChangeGroupEvent event) {
		classService.removePermissions(event.getTarget(), classService.getPermissionsToRemove(event.getTarget(), event.getOld()));
	//	classService.addAllPermissions(character, event.getNew());
	}
}
