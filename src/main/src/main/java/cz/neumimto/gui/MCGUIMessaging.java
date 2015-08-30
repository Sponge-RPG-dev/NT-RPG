package cz.neumimto.gui;

import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.entity.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class MCGUIMessaging implements IPlayerMessage {

    @Override
    public boolean isClientSideGui() {
        return true;
    }

    @Override
    public void sendMessage(IActiveCharacter player, String message) {

    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, long cooldown) {

    }

    @Override
    public void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills) {

    }

    @Override
    public void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillInfo center) {

    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {

    }

    @Override
    public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {

    }

    @Override
    public void sendManaStatus(IActiveCharacter character, float currentMana, float maxMana, float reserved) {

    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {

    }
}
