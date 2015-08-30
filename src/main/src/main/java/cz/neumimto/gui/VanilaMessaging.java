package cz.neumimto.gui;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.*;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.inventory.custom.CustomInventoryBuilder;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class VanilaMessaging implements IPlayerMessage {

    @Inject
    Game game;

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
        sendMessage(player, Localization.ON_COOLDOWN.replaceAll("%1", message).replace("%2",String.valueOf(1000 * (cooldown / 100))));
    }

    @Override
    public void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills) {
        moveSkillTreeMenu(player,skillTree,learnedSkills,skillTree.getSkills().get(StartingPoint.name));
    }

    @Override
    public void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillInfo center) {
        game.getScheduler().createTaskBuilder().async().execute(new Runnable() {
            @Override
            public void run() {
                ItemStackBuilder itemBuilder = game.getRegistry().createItemBuilder();
                Map<String, SkillInfo> values = skillTree.getSkills();
                Set<SkillInfo> conflicts = center.getConflicts();
                Set<SkillInfo> hardDepends = center.getHardDepends();
                Set<SkillInfo> softDepends = center.getSoftDepends();

                CustomInventoryBuilder customInventoryBuilder = Inventories.customInventoryBuilder();
            }
        }).submit(NtRpgPlugin.GlobalScope.plugin);
    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        sendMessage(player,type.toMessage(effect));
    }

    @Override
    public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {

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
                    .onClick(TextActions.runCommand("/info character " + character.getName()))
                    .onHover(TextActions.showText(Texts.builder(getDetailedCharInfo(character)).build())).build();
            builder.contents(build);
        }
        builder.footer(Texts.builder("====================").color(TextColors.GREEN).build());
        builder.sendTo(character.getPlayer());
    }

    private String getDetailedCharInfo(IActiveCharacter character) {
        return   "\\u00A76L: " +character.getLevel() +
                " \\u00A7aR: " +character.getRace().getName()+
                " \\u00A7bG: " + character.getGuild().getName()+
                " \\u00A7cC: " +character.getPrimaryClass().getnClass().getName();
    }
}
