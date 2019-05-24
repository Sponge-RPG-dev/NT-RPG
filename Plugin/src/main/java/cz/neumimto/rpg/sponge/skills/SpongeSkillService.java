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

import cz.neumimto.rpg.common.reloading.Reload;
import cz.neumimto.rpg.common.reloading.ReloadService;
import cz.neumimto.rpg.common.skills.SkillServiceimpl;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

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
