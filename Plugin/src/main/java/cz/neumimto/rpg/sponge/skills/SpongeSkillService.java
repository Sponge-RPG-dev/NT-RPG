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

package cz.neumimto.rpg.sponge.skills;

import cz.neumimto.rpg.ClassService;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.skills.SkillServiceimpl;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.persistance.SkillTreeDao;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.mods.SkillExecutorCallback;
import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.ScriptSkill;
import cz.neumimto.rpg.api.skills.types.TargetedScriptSkill;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.utils.CatalogId;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.util.RegisterCatalog;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.api.logging.Log.*;

/**
 * Created by NeumimTo on 1.1.2015.
 */
@Singleton
public class SpongeSkillService extends SkillServiceimpl {

	private static int id = 0;

	@Inject
	private Game game;




	@Override
	@Reload(on = ReloadService.PLUGIN_CONFIG)
	public void initGuis() {
		int i = 0;

		for (String str : pluginConfig.SKILLTREE_RELATIONS) {
			String[] split = str.split(",");

			short k = (short) (Short.MAX_VALUE - i);
			SkillTreeInterfaceModel model = new SkillTreeInterfaceModel(Integer.parseInt(split[3]),
					Sponge.getRegistry().getType(ItemType.class, split[1]).orElse(ItemTypes.STICK),
					split[2], k);

			guiModelById.put(k, model);
			guiModelByCharacter.put(split[0].charAt(0), model);
			i++;
		}

	}
}
