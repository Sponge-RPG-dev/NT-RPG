package cz.neumimto.rpg.common.entity.players.leveling;


import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.events.character.CharacterGainedLevelEvent;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.utils.ActionResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public enum SkillTreeType {
    MANUAL {
        @Override
        public void processClassLevelUp(ActiveCharacter character, PlayerClassData playerClassData, int level) {
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
        public void processCharacterInit(ActiveCharacter character, PlayerClassData playerClassData) {
            //No need to load skilltree for character, as each character has learned skills stored in the database
        }

        @Override
        public void processLearnSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService<ActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();

            ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                characterService.learnSkill(character, playerClassData, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                character.sendMessage(actionResult.getMessage());
            }
        }

        @Override
        public void processUpgradeSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService<ActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canUpgradeSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                characterService.upgradeSkill(character, skillInfo, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                character.sendMessage(actionResult.getMessage());
            }
        }

        @Override
        public void processRefundSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService<ActiveCharacter> characterService = Rpg.get().getCharacterService();
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canRefundSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                CharacterSkill characterSkill = characterService.refundSkill(character, skillInfo, iSkill);

                CompletableFuture.runAsync(() -> {
                    characterService.save(character.getCharacterBase());
                    Rpg.get().getCharacterService().removePersistantSkill(characterSkill);
                }, Rpg.get().getAsyncExecutor()).exceptionally(throwable -> {
                    Log.error("Could not refund a skillpoint ", throwable);
                    return null;
                });
            } else {
                character.sendMessage(actionResult.getMessage());
            }
        }
    },
    AUTO {
        @Override
        public void processClassLevelUp(ActiveCharacter character, PlayerClassData playerClassData, int level) {
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
                    SkillData skillData = stringSkillDataEntry.getValue();
                    PlayerSkillContext playerSkillContext = new PlayerSkillContext(classDefinition, skillData.getSkill(), character);
                    playerSkillContext.setLevel(1);
                    playerSkillContext.setSkillData(skillData);

                    Rpg.get().getCharacterService().addSkill(character, playerClassData, playerSkillContext);
                }
            }
        }

        @Override
        public void processCharacterInit(ActiveCharacter character, PlayerClassData playerClassData) {
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            SkillTree skillTree = classDefinition.getSkillTree();
            if (skillTree == null) {
                return;
            }
            int level = playerClassData.getLevel();
            Map<String, SkillData> skills = skillTree.getSkills();

            for (Map.Entry<String, SkillData> stringSkillDataEntry : skills.entrySet()) {
                if (stringSkillDataEntry.getValue().getMinPlayerLevel() <= level) {
                    SkillData skillData = stringSkillDataEntry.getValue();
                    PlayerSkillContext playerSkillContext = new PlayerSkillContext(classDefinition, skillData.getSkill(), character);
                    playerSkillContext.setLevel(1);
                    playerSkillContext.setSkillData(skillData);

                    Rpg.get().getCharacterService().addSkill(character, playerClassData, playerSkillContext);
                }
            }
        }

        @Override
        public void processLearnSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        @Override
        public void processUpgradeSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        @Override
        public void processRefundSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            sendErrorMessage(character, playerClassData.getClassDefinition().getName());
        }

        private void sendErrorMessage(ActiveCharacter player, String className) {
            String aClass = Rpg.get().getLocalizationService().translate(LocalizationKeys.NOT_ALLOWED_MANUAL_SKILLTREE_MANAGEMENT, Arg.arg("class", className));
            player.sendMessage(aClass);
        }
    };

    public abstract void processClassLevelUp(ActiveCharacter character, PlayerClassData playerClassData, int level);

    public abstract void processCharacterInit(ActiveCharacter character, PlayerClassData playerClassData);

    public abstract void processLearnSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processUpgradeSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processRefundSkill(ActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);
}
