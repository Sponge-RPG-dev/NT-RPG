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
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillData;
import cz.neumimto.skills.SkillTree;
import cz.neumimto.skills.StartingPoint;
import cz.neumimto.utils.ItemStackUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
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
        for (CharacterBase characterBase : characterBases) {
            LoreData loreData = ItemStackUtils.setLore(Texts.of("Level: " + characterBase.getLevel())
                    , Texts.of("Primary class: " + characterBase.getPrimaryClass())
                    , Texts.of("Guild: " + characterBase.getGuild())
                    , Texts.of("Race: " + characterBase.getRace())
                    , Texts.of("Last time played: " + characterBase.updated.toString()));
            list.add(b.itemType(ItemTypes.BOOK)
                    .itemData(ItemStackUtils.setDisplayName(Texts.of(characterBase.getName())))
                    .itemData(loreData)
                    .quantity(characterBase.getLevel()).build());
            b.reset();
        }
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
        return "\\u00A76L: " + character.getLevel() +
                " \\u00A7aR: " + character.getRace().getName() +
                " \\u00A7bG: " + character.getGuild().getName() +
                " \\u00A7cC: " + character.getPrimaryClass().getnClass().getName();
    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
        character.sendMessage(getDetailedCharInfo(target));
    }
}
