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

package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;


@ResourceLoader.Command
public class CommandAdmin extends CommandBase {

    @Inject
    private SkillService skillService;

    @Inject
    private EffectService effectService;

    @Inject
    private CharacterService characterService;

    @Inject
    private Logger logger;

    @Inject
    private RWService runewordService;

    @Inject
    private InventoryService inventoryService;

    public CommandAdmin() {
        setDescription("Bypasses many plugin restrictions, allows you to force execute skill, set character properties..., bad use of this command may breaks plugin mechanics or cause exceptions.");
        setPermission("ntrpg.superadmin");
        setUsage("nadmin");
        addAlias("nadmin");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] a = s.split(" ");
        if (a[0].equalsIgnoreCase("use")) {
            if (!(commandSource instanceof Player)) {
                logger.warn("Can't be executed from console");
                return CommandResult.empty();
            }
            if (a[1].equalsIgnoreCase("skill")) {
                if (a.length < 2) {
                     commandSource.sendMessage(Text.of("/nadmin use skill {skillname} [level]"));
                    return CommandResult.empty();
                }
                ISkill skill = skillService.getSkill(a[2]);
                SkillSettings defaultSkillSettings = skill.getDefaultSkillSettings();
                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
                if (character.isStub())
                    throw new RuntimeException("Character is required even for an admin.");
                int level = 1;
                if (a.length == 4)
                    level = Integer.parseInt(a[3]);
                if (skill instanceof ActiveSkill) {
                    ExtendedSkillInfo extendedSkillInfo = new ExtendedSkillInfo();
                    extendedSkillInfo.setLevel(level);
                    SkillData skillData = new SkillData(skill.getName());
                    skillData.setSkillSettings(defaultSkillSettings);
                    extendedSkillInfo.setSkillData(skillData);
                    extendedSkillInfo.setSkill(skill);
                    ActiveSkill askill = (ActiveSkill) skill;
                    askill.cast(character, extendedSkillInfo);
                }
            }
        } else if (a[0].equalsIgnoreCase("set")) {

        } else if (a[0].equalsIgnoreCase("delete")) {

        } else if (a[0].equalsIgnoreCase("enchantment")) {
            if (a[1].equalsIgnoreCase("add")) {
                String name = a[2];
                name = name.replaceAll("_"," ");
                IGlobalEffect globalEffect = effectService.getGlobalEffect(name);
                if (globalEffect == null) {
                    commandSource.sendMessage(Text.of(Localization.NON_EXISTING_GLOBAL_EFFECT));
                } else {
                    Player pl = (Player) commandSource;
                    if (pl.getItemInHand().isPresent()) {
                        ItemStack itemStack = pl.getItemInHand().get();
                        List<Text> texts = ItemStackUtils.addItemEffect(itemStack, globalEffect, Integer.parseInt(a[3]));
                        itemStack.offer(Keys.ITEM_LORE, texts);
                        pl.setItemInHand(itemStack);
                        pl.sendMessage(Text.of("Enchantment " + globalEffect.getName()+" added"));
                    } else {
                        pl.sendMessage(Text.of(Localization.NO_ITEM_IN_HAND));
                    }
                }
            } else if (a[1].equalsIgnoreCase("remove")) {

            }
        } else if (a[0].equalsIgnoreCase("socket")) {
            Player pl = (Player) commandSource;
            Optional<ItemStack> itemInHand = pl.getItemInHand();
            if (itemInHand.isPresent()) {
                ItemStack itemStack = runewordService.createSockets(itemInHand.get(),Integer.parseInt(a[1]));
                pl.setItemInHand(itemStack);
            }
        } else if (a[0].equalsIgnoreCase("rune")) {
            Rune r = runewordService.getRune(a[1]);
            if (r != null) {
                Player pl = (Player) commandSource;
                ItemStack is = runewordService.toItemStack(r);
                pl.setItemInHand(is);
            }
        } else if (a[0].equalsIgnoreCase("charm")) {

        }
        return CommandResult.success();
    }
}
