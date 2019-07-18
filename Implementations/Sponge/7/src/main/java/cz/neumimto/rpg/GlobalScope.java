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
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.exp.ExperienceService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import org.spongepowered.api.Game;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class GlobalScope {

    @Inject
    public SpongeCharacterService characterService;

    @Inject
    public IEffectService effectService;

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
    public SpongeDamageService damageService;

    @Inject
    public SpongeInventoryService inventorySerivce;

    @Inject
    public RWService runewordService;

    @Inject
    public SpongeEntityService entityService;

    @Inject
    public SpongePartyService partyService;

    @Inject
    public IPropertyService spongePropertyService;

    @Inject
    public SpongeItemService itemService;

    @Inject
    public Injector injector;

    @Inject
    public IScriptEngine jsLoader;

    @Inject
    public ResourceLoader resourceLoader;

    @Inject
    public ExperienceService experienceService;

    @Inject
    public SpongeInventoryService spongeInventoryService;

    @Inject
    public VanillaMessaging vanillaMessaging;

    @Inject
    public ParticleDecorator particleDecorator;

    @Inject
    public RWService rwService;

    @Inject
    public Gui gui;

    @Inject
    public LocalizationService localizationService;

    @Inject
    public EventFactoryService eventFactory;

    @Inject
    public AssetService assetService;
}
