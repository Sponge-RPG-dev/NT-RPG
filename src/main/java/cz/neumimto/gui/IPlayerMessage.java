package cz.neumimto.gui;

import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public interface IPlayerMessage {
    boolean isClientSideGui();

    public void sendMessage(IActiveCharacter player, String message);

    public void sendCooldownMessage(IActiveCharacter player, String message, long cooldown);

    void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills);

    void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillInfo center);

    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect);

    void invokeCharacterMenu(Player player, List<CharacterBase> characterBases);

    void sendManaStatus(IActiveCharacter character, float currentMana, float maxMana, float reserved);

    void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target);
}
