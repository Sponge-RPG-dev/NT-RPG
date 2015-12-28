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

package cz.neumimto.gui;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.ExtendedNClass;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillData;
import cz.neumimto.skills.SkillTree;
import cz.neumimto.skills.StartingPoint;
import cz.neumimto.utils.ItemStackUtils;
import cz.neumimto.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class VanilaMessaging implements IPlayerMessage {

    @Inject
    private Game game;

    @Override
    public boolean isClientSideGui() {
        return false;
    }

    @Override
    public void sendMessage(IActiveCharacter player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, long cooldown) {
        sendMessage(player, Localization.ON_COOLDOWN.replaceAll("%1", message).replace("%2", String.valueOf(1000 * (cooldown / 100))));
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
    public void sendManaStatus(IActiveCharacter character, float currentMana, float maxMana, float reserved) {
        TextBuilder.Literal b = Texts.builder("Mana: " + currentMana).color(TextColors.BLUE);
        if (reserved != 0) {
            b.append(Texts.builder(" / " + (maxMana - reserved)).color(TextColors.DARK_RED).build());
        }
        b.append(Texts.builder(" | " + maxMana).color(TextColors.GRAY).build());
        character.getPlayer().sendMessage(b.build());
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
        PaginationService paginationService = game.getServiceManager().provide(PaginationService.class).get();
        PaginationBuilder builder = paginationService.builder();
        builder.title(Texts.builder("=====================").color(TextColors.GREEN).build());
        for (CharacterBase characterBase : target) {
            Text.Literal build = Texts.builder(character.getName() + "   ")
                    .onClick(TextActions.runCommand("/info character " + characterBase.getName()))
                    .onHover(TextActions.showText(Texts.builder(getDetailedCharInfo(character)).build())).build();
            builder.contents(build);
        }
        builder.footer(Texts.builder("====================").color(TextColors.GREEN).build());
        builder.sendTo(character.getPlayer());
    }


    private String getDetailedCharInfo(IActiveCharacter character) {
        Text text = Texts.builder("Level").color(TextColors.YELLOW).append(
                    Texts.builder("Race").color(TextColors.RED).append(
                    Texts.builder("Guild").color(TextColors.AQUA).append(
                    Texts.builder("Class").color(TextColors.GOLD).build()
                    ).build()).build()).build();
        return text.toString();
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
        character.sendMessage(getDetailedCharInfo(target));
    }

    @Override
    public void showExpChange(IActiveCharacter character,String classname,double expchange) {
        Player player = character.getPlayer();
        player.sendMessage(Texts.of(classname+" expchange: +" + expchange));
    }

    @Override
    public void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage(Texts.of("Level up: "+clazz.getnClass().getName()+ " - " + level));
    }

    @Override
    public void sendStatus(IActiveCharacter character) {
        Player player = character.getPlayer();
        String q = "HP: "+character.getHealth().getValue()+"/"+character.getHealth().getMaxValue()+"/"+character.getHealth().getRegen();
        player.sendMessage(Texts.of(q));
        q = "Mana: "+character.getMana().getValue()+"/"+character.getMana().getMaxValue()+"/"+character.getMana().getRegen();
        player.sendMessage(Texts.of(q));
        q = "Attribute points: " + character.getAttributePoints() + "\n";
        q += "Skill points: " + character.getSkillPoints();
        player.sendMessage(Texts.of(q));
        player.sendMessage(Texts.of("------------"));
        q = "Allocated attribute points: " + character.getCharacterBase().getUsedAttributePoints() + "\n";
        q += "Allocated skill points: " + character.getCharacterBase().getUsedSkillPoints();
        player.sendMessage(Texts.of(q));
        q = "Class: " + character.getPrimaryClass().getnClass().getName() + ", Level: " + character.getLevel();
        player.sendMessage(Texts.of(q));
        q = "Progress- Total:" + character.getPrimaryClass().getExperiences()+"/"+character.getPrimaryClass().getnClass().getTotalExp();
        character.sendMessage(q);
        q = "          Level: " + character.getPrimaryClass().getExperiencesFromLevel()+"/"+character.getPrimaryClass().getnClass().getLevels()[character.getPrimaryClass().getLevel()];
        character.sendMessage(q);

    }
}
