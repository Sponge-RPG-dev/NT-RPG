package cz.neumimto.rpg.players.leveling;


import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.events.CharacterGainedLevelEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.spongepowered.api.Sponge;

import java.util.Map;

public enum SkillTreeType {
    TREE {
        @Override
        public void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level) {
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            SkillTree skillTree = classDefinition.getSkillTree();
            if (skillTree == null) {
                return;
            }

            CharacterGainedLevelEvent event =
                    new CharacterGainedLevelEvent(character, playerClassData, level,
                            playerClassData.getClassDefinition().getSkillpointsPerLevel(),
                            playerClassData.getClassDefinition().getAttributepointsPerLevel());

            Sponge.getGame().getEventManager().post(event);

            NtRpgPlugin.GlobalScope.characterService.addSkillPoint(character, playerClassData, event.getSkillpointsPerLevel());
        }

        @Override
        public void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData) {

        }
    },
    FLAT {
        @Override
        public void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level) {
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            SkillTree skillTree = classDefinition.getSkillTree();
            if (skillTree == null) {
                return;
            }
            CharacterGainedLevelEvent event =
                    new CharacterGainedLevelEvent(character, playerClassData, level, 0, classDefinition.getAttributepointsPerLevel());
            Sponge.getGame().getEventManager().post(event);

            Map<String, SkillData> skills = skillTree.getSkills();
            for (Map.Entry<String, SkillData> stringSkillDataEntry : skills.entrySet()) {
                if (stringSkillDataEntry.getValue().getMinPlayerLevel() == level) {
                    NtRpgPlugin.GlobalScope.characterService.addSkill(character, playerClassData, stringSkillDataEntry.getValue().getSkill());
                }
            }
        }

        @Override
        public void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData) {
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            SkillTree skillTree = classDefinition.getSkillTree();
            if (skillTree == null) {
                return;
            }
            int level = playerClassData.getLevel();
        }
    };

    public abstract void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level);

    public abstract void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData);
}
