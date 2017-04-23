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

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.TestAction;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;


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

    @Inject
    private JSLoader jsLoader;

    public CommandAdmin() {
        setDescription("Bypasses many plugin restrictions, allows you to force execute skill, set character properties..., bad use of this command may breaks plugin mechanics or cause exceptions.");
        setPermission("ntrpg.superadmin");
        setUsage("nadmin");
        addAlias("nadmin");
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] a = s.split(" ");
        if (!(commandSource instanceof Player)) {
            logger.warn("Can't be executed from console");
            return CommandResult.empty();
        }
        Player player = (Player) commandSource;
        /*
        if (player.hasPermission("ntrpg.superadmin")) {
            return CommandResult.empty();
        }
        */
        if (a[0].equalsIgnoreCase("use")) {

            if (a[1].equalsIgnoreCase("skill")) {
                if (a.length < 2) {
                    commandSource.sendMessage(Text.of("/nadmin use skill {skillname} [level]"));
                    return CommandResult.empty();
                }
                ISkill skill = skillService.getSkill(a[2]);
                SkillSettings defaultSkillSettings = skill.getDefaultSkillSettings();
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.isStub())
                    throw new RuntimeException("Character is required even for an admin.");
                int level = 1;
                if (a.length == 4)
                    level = Integer.parseInt(a[3]);
                if (skill instanceof ActiveSkill) {
                    Long l = System.nanoTime();
                    Vector3d vector3d = character.getPlayer().get(Keys.VELOCITY).get();
                    vector3d.add(0,8,0);
                    character.getPlayer().offer(Keys.VELOCITY, vector3d);
                    ExtendedSkillInfo extendedSkillInfo = new ExtendedSkillInfo();
                    extendedSkillInfo.setLevel(level);
                    SkillData skillData = new SkillData(skill.getName());
                    skillData.setSkillSettings(defaultSkillSettings);
                    extendedSkillInfo.setSkillData(skillData);
                    extendedSkillInfo.setSkill(skill);
                    ActiveSkill askill = (ActiveSkill) skill;
                    askill.cast(character, extendedSkillInfo, null);
                    Long e = System.nanoTime();
                    character.sendMessage("Exec Time: " + TimeUnit.MILLISECONDS.convert(e-l, TimeUnit.NANOSECONDS));;
                }
            }
        } else if (a[0].equalsIgnoreCase("set")) {

        } else if (a[0].equalsIgnoreCase("delete")) {

        } else if (a[0].equalsIgnoreCase("enchantment")) {
            if (a[1].equalsIgnoreCase("add")) {
                String name = a[2];
                name = name.replaceAll("_", " ");
                IGlobalEffect globalEffect = effectService.getGlobalEffect(name);
                if (globalEffect == null) {
                    commandSource.sendMessage(Text.of(Localization.NON_EXISTING_GLOBAL_EFFECT));
                } else {
                    if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                        ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
                        CustomItemData itemData = inventoryService.getItemData(itemStack);
                        itemData.enchantements().put(globalEffect.getName(), a[3]);
                        player.sendMessage(Text.of("Enchantment " + globalEffect.getName() + " added"));
                    } else {
                        player.sendMessage(Text.of(Localization.NO_ITEM_IN_HAND));
                    }
                }
            } else if (a[1].equalsIgnoreCase("remove")) {

            }
        } else if (a[0].equalsIgnoreCase("socket")) {
            Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
            if (itemInHand.isPresent()) {
                ItemStack itemStack = runewordService.createSockets(itemInHand.get(), Integer.parseInt(a[1]));
                player.setItemInHand(HandTypes.MAIN_HAND,itemStack);
            }
        } else if (a[0].equalsIgnoreCase("rune")) {
            Rune r = runewordService.getRune(a[1]);
            if (r != null) {
                ItemStack is = runewordService.toItemStack(r);
                player.setItemInHand(HandTypes.MAIN_HAND,is);
            }
        } else if (a[0].equalsIgnoreCase("charm")) {

        } else if (a[0].equalsIgnoreCase("imbue")) {
            List<Rune> r = new ArrayList<>();
            for (int i = 1; i < a.length; i++) {
                Rune rune = runewordService.getRune(a[i]);
                if (rune == null) {
                    commandSource.sendMessage(Text.of("Rune " + a[i] + " does not exist"));
                    return CommandResult.empty();
                }
                r.add(rune);
            }
            Player p = (Player) commandSource;
            Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
            if (itemInHand.isPresent()) {
                ItemStack itemStack = itemInHand.get();
                for (Rune rune1 : r) {
                    itemStack = runewordService.insertRune(itemStack, rune1.getName());
                    itemStack = runewordService.findRuneword(itemStack);
                    p.setItemInHand(HandTypes.MAIN_HAND,itemStack);
                }
            }

        } else if (a[0].equalsIgnoreCase("runeword")) {
            RuneWord runeword = runewordService.getRuneword(a[1]);
            Player p = (Player) commandSource;
            Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
            if (itemInHand.isPresent()) {
                ItemStack itemStack = itemInHand.get();
                ItemStack itemStack1 = runewordService.reBuildRuneword(itemStack, runeword);
                p.setItemInHand(HandTypes.MAIN_HAND,itemStack1);
            }
        } else if (a[0].equalsIgnoreCase("exp")) {
            if (a[1].equalsIgnoreCase("add")) {
                Optional<Player> player2 = Sponge.getServer().getPlayer(a[2]);
                if (player2.isPresent()) {
                    IActiveCharacter character = characterService.getCharacter(player2.get().getUniqueId());
                    Set<ExtendedNClass> classes = character.getClasses();
                    for (ExtendedNClass aClass : classes) {
                        if (aClass.getConfigClass().getName().equalsIgnoreCase(a[3])) {
                            characterService.addExperiences(character,Double.valueOf(a[4]),aClass,false);
                        }
                    }
                }
            }
        } else if (a[0].equalsIgnoreCase("test.action")) {
            if (PluginConfig.DEBUG) {
                String methodcall = a[1];
                try {
                    Object o = IoC.get().build(TestAction.class);
                    Method method = TestAction.class.getMethod(methodcall, IActiveCharacter.class);
                    method.invoke(o,characterService.getCharacter(player.getUniqueId()));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalStateException("Only avalaible in debug mode");
            }
        } else if (a[0].equalsIgnoreCase("reloadjs")) {
            if (!PluginConfig.DEBUG) {
                commandSource.sendMessage(Text.of("Reloading is allowed only in debug mode"));
                return CommandResult.success();
            }
            jsLoader.initEngine();

            int i = 1;
            String q = null;
            while (i < a.length) {
                q = a[i];
                if (q.equalsIgnoreCase("skills") | q.equalsIgnoreCase("s")) {
                    jsLoader.reloadSkills();
                }
                if (q.equalsIgnoreCase("attributes") | q.equalsIgnoreCase("a")) {
                    jsLoader.reloadAttributes();
                }
                if (q.equalsIgnoreCase("globaleffects") | q.equalsIgnoreCase("g")) {
                    jsLoader.reloadGlobalEffects();
                }
                i++;
            }
        }
        return CommandResult.success();
    }
}
