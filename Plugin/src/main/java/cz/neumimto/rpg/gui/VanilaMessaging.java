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
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.EffectStatusType;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.persistance.DirectAccessDao;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillTree;
import cz.neumimto.rpg.skills.StartingPoint;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.rpg.utils.model.CharacterListModel;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;

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

    @Inject
    private EffectService effectService;

    @Inject
    private NtRpgPlugin plugin;

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
        if (effect == null) {
            effect = new BossBarExpNotifier(character);
            effectService.addEffect(effect,character);
        }
        BossBarExpNotifier bossbar = (BossBarExpNotifier) effect;
        bossbar.setLevel(character.getPrimaryClass().getLevel());
        bossbar.notifyExpChange(classname, expchange);
    }

    @Override
    public void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level) {
        Player player = character.getPlayer();
        player.sendMessage(Text.of("Level up: " + clazz.getConfigClass().getName() + " - " + level));
    }

    @Override
    public void sendStatus(IActiveCharacter character) {
        CharacterBase base = character.getCharacterBase();

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        builder.title(Text.of(character.getName(),Color.YELLOW));
        builder.padding(Text.of("═",Color.GRAY));

        List<Text> content = new ArrayList<>();
        Set<CharacterClass> characterClasses = base.getCharacterClasses();
        for (CharacterClass cc : characterClasses) {
            Text t = Text.builder().append(Text.of(Utils.capitalizeFirst(cc.getName()),Color.GREEN))
                                    .append(Text.of(" - ",TextColors.GRAY))
                                    .append(Text.of(cc.getSkillPoints(),TextColors.BLUE))
                                    .append(Text.of(String.format("(%s)",cc.getUsedSkillPoints()),TextColors.GRAY))

                    .toText();
            content.add(t);
        }
        content.add(Text.builder().append(Text.of("Attribute points: ",TextColors.GREEN))
                                .append(Text.of(character.getCharacterBase().getAttributePoints(),TextColors.AQUA))
                                .append(Text.of(String.format("(%s)",character.getCharacterBase().getUsedAttributePoints(),TextColors.GRAY))).toText());
        Player player = character.getPlayer();

        builder.contents(content);
        builder.sendTo(character.getPlayer());
    }

    @Override
    public void showAvalaibleClasses(IActiveCharacter character) {
        Collection<ConfigClass> classes = groupService.getClasses();
        List<ItemStack> list = new ArrayList<>();
        for (ConfigClass aClass : classes) {
            ItemType type = aClass.getItemType();
            if (type == null) {
                type = ItemTypes.DIAMOND_SWORD;
            }
            ItemStack a = ItemStack.of(type, 1);
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
        CustomInventory.Builder builder = CustomInventory.builder();

        builder.size(8);
        CustomInventory build = builder.build();

        build.set(new SlotIndex(1),ItemStack.of(ItemTypes.SHIELD,1));
        character.getPlayer().openInventory(build, Cause.of(NamedCause.of("asd",character)));
    }

    @Override
    public void sendListOfCharacters(final IActiveCharacter player, CharacterBase currentlyCreated) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        PaginationList.Builder builder = paginationService.builder();
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            DirectAccessDao build = IoC.get().build(DirectAccessDao.class);
            String query = "create new cz.neumimto.rpg.utils.model.CharacterListModel(" +
                    "c.name,d.name,d.experiences) " +
                    "from CharacterBase c left join c.characterClasses d " +
                    "where c.uuid = :id and d.name = c.primaryClass order by c.updated desc";
            Map map = new HashMap<>();
            map.put("id", player.getCharacterBase().getUuid());
            List<CharacterListModel> list = build.findList(CharacterListModel.class, query, map);
            List<Text> content = new ArrayList<Text>();
            builder.linesPerPage(5);
            builder.padding(Text.of("=",TextColors.DARK_GRAY));

            String current = player.getName();
            list.stream().forEach(a -> {
                Text.Builder b = Text.builder(" -")
                        .color(TextColors.GRAY);
                if (!a.getCharacterName().equals(current)) {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                     .append(Text.builder("SELECT").color(TextColors.GREEN).onClick(TextActions.runCommand("choose character " + a.getCharacterName())).build())
                     .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                } else {
                    b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
                     .append(Text.builder("*").color(TextColors.RED).build())
                     .append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
                }
                b.append(Text.builder(a.getCharacterName()).color(TextColors.GRAY).append(Text.of(" ")).build());
                b.append(Text.builder(a.getPrimaryClassName()).color(TextColors.AQUA).append(Text.of(" ")).build());
            b.append(Text.builder(IoC.get().build(Experience.class).));
                content.add(b.build());
            });
            builder.title(Text.of("Characters",TextColors.WHITE))
                    .contents(Text.of("Item 1"),
                            Text.of("Item 2"),
                            Text.of("Item 3"))
                    .padding(Text.of("=", Color.GRAY));
            builder.sendTo(player.getEntity());

        }).submit(plugin);
    }
}
