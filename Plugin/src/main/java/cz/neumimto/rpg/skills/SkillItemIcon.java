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

package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.gui.GuiHelper;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

/**
 * Created by ja on 31.8.2015.
 */
public class SkillItemIcon {
	public ItemType itemType;
	public String skillName;

	public ISkill skill;

	public SkillItemIcon(ISkill skill) {
		this.skillName = skill.getName();
		this.skill = skill;
		itemType = skill.getItemType();
	}

	public ItemStack toItemStack() {
		ItemStack of = GuiHelper.itemStack(itemType == null ? ItemTypes.STONE : itemType);
		of.offer(Keys.DISPLAY_NAME, Text.of(skill.getName()));
		return of;
	}


}
