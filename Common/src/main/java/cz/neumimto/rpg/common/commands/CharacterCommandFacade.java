package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.gui.SkillTreeViewModel;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.skills.tree.SkillTree;
import cz.neumimto.rpg.common.utils.ActionResult;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@Singleton
public class CharacterCommandFacade {

    @Inject
    private CharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private PartyService partyService;

    @Inject
    private ClassService classService;

    @Inject
    private PropertyServiceImpl propertyService;

    @Inject
    private InventoryService inventoryService;

    public void commandCommitAttribute(IActiveCharacter character) {
        Map<String, Integer> attributesTransaction = character.getAttributesTransaction();
        Map<AttributeConfig, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : attributesTransaction.entrySet()) {
            Optional<AttributeConfig> attributeById = propertyService.getAttributeById(entry.getKey());
            if (attributeById.isPresent()) {
                map.put(attributeById.get(), entry.getValue());
            }
        }
        ActionResult actionResult = characterService.addAttribute(character, map);
        if (actionResult.isOk()) {
            characterService.putInSaveQueue(character.getCharacterBase());
            for (Map.Entry<AttributeConfig, Integer> e : map.entrySet()) {
                character.sendMessage(e.getKey().getName() + " +" + e.getValue());
            }
            for (String s : propertyService.getAttributes().keySet()) {
                attributesTransaction.put(s, 0);
            }

        } else {
            character.sendMessage(actionResult.getMessage());
        }
    }

    public void commandCreateCharacter(UUID uuid, String name, String playerName, Consumer<ActionResult> actionResultConsumer) {
        CompletableFuture.runAsync(() -> {
            int i = characterService.canCreateNewCharacter(uuid, name);
            if (i == 1) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.REACHED_CHARACTER_LIMIT)));
            } else if (i == 2) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.CHARACTER_EXISTS)));
            } else if (i == 0) {
                CharacterBase characterBase = characterService.createCharacterBase(name, uuid, playerName);

                characterService.create(characterBase);

                String text = localizationService.translate(LocalizationKeys.CHARACTER_CREATED, Arg.arg("name", characterBase.getName()));

                actionResultConsumer.accept(ActionResult.ok(text));

                Executor executor = Rpg.get().getSyncExecutor();
                executor.execute(() -> {
                    IActiveCharacter character = characterService.getCharacter(uuid);
                    Gui.sendListOfCharacters(character, characterBase);
                });
            }
        }, Rpg.get().getAsyncExecutor()).exceptionally(throwable -> {
            Log.error("Could not create character", throwable);
            return null;
        });
    }


    public void commandSwitchCharacter(IActiveCharacter current, String nameNext, Consumer<Runnable> syncCallback) {
        if (current != null && current.getName().equalsIgnoreCase(nameNext)) {
            current.sendMessage(localizationService.translate(LocalizationKeys.ALREADY_CURRENT_CHARACTER));
            return;
        }
        inventoryService.invalidateGUICaches(current);
        CompletableFuture.runAsync(() -> {
            UUID uuid = current.getUUID();
            List<CharacterBase> playersCharacters = characterService.getPlayersCharacters(uuid);
            boolean b = false;
            for (CharacterBase playersCharacter : playersCharacters) {
                if (playersCharacter.getName().equalsIgnoreCase(nameNext)) {
                    IActiveCharacter character = characterService.createActiveCharacter(uuid, playersCharacter);
                    syncCallback.accept(new CommandSyncCallback(character, this));
                    b = true;
                    //Update characterbase#updated, so next time plazer logs it it will autoselect this character,
                    // even if it was never updated afterwards
                    characterService.save(playersCharacter);
                    break;
                }
            }
            if (!b) {
                current.sendMessage(localizationService.translate(LocalizationKeys.NON_EXISTING_CHARACTER));
            }
        }, Rpg.get().getAsyncExecutor()).exceptionally(throwable -> {
            Log.error("Could not change character", throwable);
            return null;
        });
    }

    public void openSKillTreeMenu(IActiveCharacter character, ClassDefinition classDefinition) {
        SkillTree skillTree = classDefinition.getSkillTree();
        if (skillTree == SkillTree.Default || skillTree == null) {
            character.sendMessage("Unknown class, or the class has no skilltree defined");
            return;
        }
        Set<SkillTreeViewModel> set = character.getSkillTreeViewLocation().entrySet();

        for (SkillTreeViewModel treeViewModel : set) {
            treeViewModel.setCurrent(false);
        }
        if (character.getSkillTreeViewLocation().get(skillTree.getId()) == null) {
            SkillTreeViewModel skillTreeViewModel = SkillTreeViewModel.get();
            character.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
            skillTreeViewModel.setSkillTree(skillTree);
        } else {
            ((SkillTreeViewModel) character.getSkillTreeViewLocation().get(skillTree.getId())).setCurrent(true);
        }
        Gui.openSkillTreeMenu(character);
    }

    private static class CommandSyncCallback implements Runnable {
        private final IActiveCharacter character;
        private CharacterCommandFacade facade;

        private CommandSyncCallback(IActiveCharacter character, CharacterCommandFacade facade) {
            this.character = character;
            this.facade = facade;
        }

        @Override
        public void run() {
            UUID uuid = character.getUUID();
            facade.partyService.createNewParty(character);
            facade.characterService.setActiveCharacter(uuid, character);
            facade.characterService.invalidateCaches(character);
            facade.characterService.assignPlayerToCharacter(uuid);
        }
    }

}
