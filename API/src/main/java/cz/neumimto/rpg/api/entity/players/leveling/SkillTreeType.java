package cz.neumimto.rpg.api.entity.players.leveling;


import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.events.character.CharacterGainedLevelEvent;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.messaging.MessageLevel;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.ActionResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public enum SkillTreeType {
    MANUAL {
        @Override
        public void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level) {
            CharacterGainedLevelEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterGainedLevelEvent.class);
            event.setTarget(character);
            event.setLevel(level);
            event.setPlayerClassData(playerClassData);
            event.setSkillpointsPerLevel(playerClassData.getClassDefinition().getSkillpointsPerLevel());
            event.setAttributepointsPerLevel(playerClassData.getClassDefinition().getAttributepointsPerLevel());
            Rpg.get().postEvent(event);
            Rpg.get().getCharacterService().addSkillPoint(character, playerClassData, event.getSkillpointsPerLevel());
        }

        @Override
        public void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData) {

        }

        @Override
        public void processLearnSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            ICharacterService<IActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();

            ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                characterService.learnSkill(character, playerClassData, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                MessageLevel.ERROR.sendMessage(character, actionResult.getMessage());
            }
        }

        @Override
        public void processUpgradeSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            ICharacterService<IActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canUpgradeSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                characterService.upgradeSkill(character, skillInfo, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                MessageLevel.ERROR.sendMessage(character, actionResult.getMessage());
            }
        }

        @Override
        public void processRefundSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            ICharacterService<IActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canRefundSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                CharacterSkill characterSkill = characterService.refundSkill(character, skillInfo, iSkill);

                CompletableFuture.runAsync(() -> {
                    characterService.save(character.getCharacterBase());
                    Rpg.get().getCharacterService().removePersistantSkill(characterSkill);
                }, Rpg.get().getAsyncExecutor());
            } else {
                MessageLevel.ERROR.sendMessage(character, actionResult.getMessage());
            }
        }
    },
    AUTO {
        @Override
        public void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level) {
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            SkillTree skillTree = classDefinition.getSkillTree();
            if (skillTree == null) {
                return;
            }

            CharacterGainedLevelEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterGainedLevelEvent.class);
            event.setTarget(character);
            event.setLevel(level);
            event.setPlayerClassData(playerClassData);
            event.setSkillpointsPerLevel(playerClassData.getClassDefinition().getSkillpointsPerLevel());
            event.setAttributepointsPerLevel(playerClassData.getClassDefinition().getAttributepointsPerLevel());
            Rpg.get().postEvent(event);

            Map<String, SkillData> skills = skillTree.getSkills();
            for (Map.Entry<String, SkillData> stringSkillDataEntry : skills.entrySet()) {
                if (stringSkillDataEntry.getValue().getMinPlayerLevel() == level) {
                    SkillData skillData = skillTree.getSkills().get(skillTree.getId());
                    PlayerSkillContext playerSkillContext = new PlayerSkillContext(classDefinition, skillData.getSkill(), character);
                    playerSkillContext.setLevel(1);
                    playerSkillContext.setSkillData(skillData);

                    Rpg.get().getCharacterService().addSkill(character, playerClassData, playerSkillContext);
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

        @Override
        public void processLearnSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        @Override
        public void processUpgradeSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        @Override
        public void processRefundSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        private void sendErrorMessage(IActiveCharacter player, String className) {
            String aClass = Rpg.get().getLocalizationService().translate(LocalizationKeys.NOT_ALLOWED_MANUAL_SKILLTREE_MANAGEMENT, Arg.arg("class", className));

        }
    };

    public abstract void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level);

    public abstract void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData);

    public abstract void processLearnSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processUpgradeSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processRefundSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);
}
