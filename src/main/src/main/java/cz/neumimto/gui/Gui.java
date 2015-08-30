package cz.neumimto.gui;

import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.ioc.IoC;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.entity.player.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NeumimTo on 12.2.2015.
 */
public class Gui {

    public static IPlayerMessage vanilla;

    public static IPlayerMessage mod;

    static {
        vanilla = IoC.get().build(VanilaMessaging.class);
        mod = IoC.get().build(MCGUIMessaging.class);
    }

    public static boolean isUsingClientSideGui(Player player) {
        return false;
    }

    public static IPlayerMessage getMessageTypeOf(Player player) {
        if (isUsingClientSideGui(player))
            return mod;
        return vanilla;
    }

    public static void sendMessage(IActiveCharacter player, String message) {
        getMessageTypeOf(player.getPlayer()).sendMessage(player,message);
    }

    public static void sendCooldownMessage(IActiveCharacter player, String skillname, long cooldown) {
        getMessageTypeOf(player.getPlayer()).sendCooldownMessage(player,skillname,cooldown);
    }

    public static void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree,ConcurrentHashMap<String,Integer> learnedSkills) {
        getMessageTypeOf(player.getPlayer()).openSkillTreeMenu(player,skillTree,learnedSkills);
    }

    public static void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
        getMessageTypeOf(player.getPlayer()).sendEffectStatus(player,type,effect);
    }

    public static void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
        getMessageTypeOf(player).invokeCharacterMenu(player, characterBases);
    }

    public static void sendManaStatus(IActiveCharacter character, float currentMana, float maxMana, float reserved) {
        getMessageTypeOf(character.getPlayer()).sendManaStatus(character,currentMana,maxMana,reserved);
    }

    public static void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
        getMessageTypeOf(character.getPlayer()).sendPlayerInfo(character,target);
    }
}
