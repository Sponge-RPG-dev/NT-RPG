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

package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.commands.CommandService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.ItemService;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.parties.PartyService;
import cz.neumimto.rpg.properties.PropertyService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.SkillService;
import org.spongepowered.api.Game;

import javax.inject.Inject;
import javax.inject.Singleton;

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
	public ClassService classService;

	@Inject
	public CommandService commandService;

	@Inject
	public SkillService skillService;

	@Inject
	public NtRpgPlugin plugin;

	@Inject
	public Game game;

	@Inject
	public DamageService damageService;

	@Inject
	public InventoryService inventorySerivce;

	@Inject
	public RWService runewordService;

	@Inject
	public EntityService entityService;

	@Inject
	public PartyService partyService;

	@Inject
	public PropertyService propertyService;

	@Inject
	public ItemService itemService;

	@Inject
	public Injector injector;

	@Inject
	public JSLoader jsLoader;

	@Inject
	public ResourceLoader resourceLoader;
}
