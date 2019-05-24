package cz.neumimto.rpg.players.leveling;


import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.ActionResult;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.events.character.CharacterGainedLevelEvent;
import cz.neumimto.rpg.persistance.DirectAccessDao;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import org.spongepowered.api.Sponge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public enum SkillTreeType {
    MANUAL {
        @Override
        public void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level) {
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

        @Override
        public void processLearnSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService characterService = NtRpgPlugin.GlobalScope.characterService;
            ClassDefinition classDefinition = playerClassData.getClassDefinition();

            ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                characterService.learnSkill(character, playerClassData, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                character.getPlayer().sendMessage(actionResult.getErrorMesage());
            }
        }

        @Override
        public void processUpgradeSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService characterService = NtRpgPlugin.GlobalScope.characterService;
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canUpgradeSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                characterService.upgradeSkill(character, skillInfo, iSkill);
                characterService.putInSaveQueue(character.getCharacterBase());
            } else {
                character.getPlayer().sendMessage(actionResult.getErrorMesage());
            }
        }

        @Override
        public void processRefundSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill) {
            CharacterService characterService = NtRpgPlugin.GlobalScope.characterService;
            ClassDefinition classDefinition = playerClassData.getClassDefinition();
            ActionResult actionResult = characterService.canRefundSkill(character, classDefinition, iSkill);
            if (actionResult.isOk()) {
                PlayerSkillContext skillInfo = character.getSkillInfo(iSkill);
                CharacterSkill characterSkill = characterService.refundSkill(character, skillInfo, iSkill);

                CompletableFuture.runAsync(() -> {
                    characterService.save(character.getCharacterBase());
                    DirectAccessDao dad = NtRpgPlugin.GlobalScope.injector.getInstance(DirectAccessDao.class);
                    //language=HQL
                    Map<String, Object> params = new HashMap<>();
                    params.put("id", characterSkill);
                    dad.update("delete from CharacterSkill where skillId = :id", params);
                }, NtRpgPlugin.asyncExecutor);
            } else {
                character.getPlayer().sendMessage(actionResult.getErrorMesage());
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
            CharacterGainedLevelEvent event =
                    new CharacterGainedLevelEvent(character, playerClassData, level, 0, classDefinition.getAttributepointsPerLevel());
            Sponge.getGame().getEventManager().post(event);

            Map<String, SkillData> skills = skillTree.getSkills();
            for (Map.Entry<String, SkillData> stringSkillDataEntry : skills.entrySet()) {
                if (stringSkillDataEntry.getValue().getMinPlayerLevel() == level) {
                    SkillData skillData = skillTree.getSkills().get(skillTree.getId());
                    PlayerSkillContext playerSkillContext = new PlayerSkillContext(classDefinition, skillData.getSkill(), character);
                    playerSkillContext.setLevel(1);
                    playerSkillContext.setSkillData(skillData);

                    NtRpgPlugin.GlobalScope.characterService.addSkill(character, playerClassData, playerSkillContext);
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
            player.sendMessage(Localizations.NOT_ALLOWED_MANUAL_SKILLTREE_MANAGEMENT.toText(Arg.arg("class", className)));
        }
    };

    public abstract void processClassLevelUp(IActiveCharacter character, PlayerClassData playerClassData, int level);

    public abstract void processCharacterInit(IActiveCharacter character, PlayerClassData playerClassData);

    public abstract void processLearnSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processUpgradeSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);

    public abstract void processRefundSkill(IActiveCharacter character, PlayerClassData playerClassData, ISkill iSkill);
}
