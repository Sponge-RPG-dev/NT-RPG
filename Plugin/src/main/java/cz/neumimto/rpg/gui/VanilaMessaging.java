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

package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.NClass;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillTree;
import cz.neumimto.rpg.skills.StartingPoint;
import org.spongepowered.api.Game;
import org.spongepowered.api.boss.BossBar;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class VanilaMessaging implements IPlayerMessage {

    @Inject
    private Game game;

    @Inject
    private GroupService groupService;


    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void invokerDefaultMenu(IActiveCharacter character) {

    }

    @Override
    public void sendMessage(IActiveCharacter player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, double cooldown) {
        sendMessage(player, Localization.ON_COOLDOWN.replaceAll("%1", message).replace("%2", String.valueOf(cooldown)));
    }

    @Override
    public void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills) {
        moveSkillTreeMenu(player, skillTree, learnedSkills, skillTree.getSkills().get(StartingPoint.name));
    }

    @Override
    public void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillData center) {
        game.getScheduler().createTaskBuilder().async().execute(new Runnable() {
            @Override
            public void run() {
                ItemStack.Builder itemBuilder = ItemStack.builder();
                Map<String, SkillData> values = skillTree.getSkills();
                Set<SkillData> conflicts = center.getConflicts();
                Set<SkillData> hardDepends = center.getHardDepends();
                Set<SkillData> softDepends = center.getSoftDepends();
                //TODO inventory
            }
        }).submit(NtRpgPlugin.GlobalScope.plugin);
    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        sendMessage(player, type.toMessage(effect));
    }

    @Override
    public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
        ItemStack.Builder b = ItemStack.builder();
        List<ItemStack> list = new ArrayList<>();
        //todo
    }

    @Override
    public void sendManaStatus(IActiveCharacter character, double currentMana, double maxMana, double reserved) {
        Text.Builder b = Text.builder("Mana: " + currentMana).color(TextColors.BLUE);
        if (reserved != 0) {
            b.append(Text.builder(" / " + (maxMana - reserved)).color(TextColors.DARK_RED).build());
        }
        b.append(Text.builder(" | " + maxMana).color(TextColors.GRAY).build());
        character.getPlayer().sendMessage(b.build());
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
        PaginationService paginationService = game.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        builder.title(Text.builder("=====================").color(TextColors.GREEN).build());
        for (CharacterBase characterBase : target) {

        }
        builder.footer(Text.builder("====================").color(TextColors.GREEN).build());
        builder.sendTo(character.getPlayer());
    }


    private String getDetailedCharInfo(IActiveCharacter character) {
        Text text = Text.builder("Level").color(TextColors.YELLOW).append(
                Text.builder("Race").color(TextColors.RED).append(
                        Text.builder("Guild").color(TextColors.AQUA).append(
                                Text.builder("Class").color(TextColors.GOLD).build()
                        ).build()).build()).build();
        return text.toString();
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
        character.sendMessage(getDetailedCharInfo(target));
    }

    @Override
    public void showExpChange(IActiveCharacter character, String classname, double expchange) {
        Player player = character.getPlayer();
        IEffect effect = character.getEffect(BossBarExpNotifier.class);
        if (effect != null) {
            BossBarExpNotifier bossbar = (BossBarExpNotifier) effect;
            bossbar.setLevel(character.getPrimaryClass().getLevel());
            bossbar.notifyExpChange(classname, expchange);
        }
    }

    @Override
    public void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage(Text.of("Level up: " + clazz.getnClass().getName() + " - " + level));
    }

    @Override
    public void sendStatus(IActiveCharacter character) {
        Player player = character.getPlayer();
        String q = "HP: " + character.getHealth().getValue() + "/" + character.getHealth().getMaxValue() + "/" + character.getHealth().getRegen();
        player.sendMessage(Text.of(q));
        q = "Mana: " + character.getMana().getValue() + "/" + character.getMana().getMaxValue() + "/" + character.getMana().getRegen();
        player.sendMessage(Text.of(q));
        q = "Attribute points: " + character.getAttributePoints() + "\n";
        q += "Skill points: " + character.getSkillPoints();
        player.sendMessage(Text.of(q));
        player.sendMessage(Text.of("------------"));
        q = "Allocated attribute points: " + character.getCharacterBase().getUsedAttributePoints() + "\n";
        q += "Allocated skill points: " + character.getCharacterBase().getUsedSkillPoints();
        player.sendMessage(Text.of("------------"));
        Map<String, Integer> attributes = character.getCharacterBase().getAttributes();
        for (Map.Entry<String, Integer> a : attributes.entrySet()) {
            character.sendMessage(a.getKey() + ": " + a.getValue());
        }
        player.sendMessage(Text.of("------------"));
        player.sendMessage(Text.of(q));
        q = "Class: " + character.getPrimaryClass().getnClass().getName() + ", Level: " + character.getLevel();
        player.sendMessage(Text.of(q));
        q = "Progress- Total:" + character.getPrimaryClass().getExperiences() + "/" + character.getPrimaryClass().getnClass().getTotalExp();
        character.sendMessage(q);
        q = "          Level: " + character.getPrimaryClass().getExperiencesFromLevel() + "/" + character.getPrimaryClass().getnClass().getLevels()[character.getPrimaryClass().getLevel()];
        character.sendMessage(q);
    }

    @Override
    public void showAvalaibleClasses(IActiveCharacter character) {
        Collection<NClass> classes = groupService.getClasses();
        List<ItemStack> list = new ArrayList<>();
        for (NClass aClass : classes) {
            ItemStack a = ItemStack.of(aClass.getItemType(), 1);
            a.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, ""));
            List<Text> lore = new ArrayList<>();
            double[] levels = aClass.getLevels();
            if (levels != null) {
                lore.add(Text.of(TextColors.RED, "Max Level/Total exp: " + levels.length + "/" + aClass.getTotalExp()));
            }
            lore.add(Text.of(TextColors.GREEN, aClass.getDescription()));
            lore.add(Text.of(TextColors.DARK_GRAY, Localization.CLASS_INVENTORYMENU_FOOTER));
            a.offer(Keys.ITEM_LORE, lore);
            list.add(a);
        }
    }
}
