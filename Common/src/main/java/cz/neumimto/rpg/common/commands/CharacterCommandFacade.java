package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.common.entity.PropertyService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@Singleton
public class CharacterCommandFacade {

    @Inject
    private ICharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private PartyService partyService;

    @Inject
    private PropertyService propertyService;

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
        } else {
            character.sendMessage(actionResult.getMessage());
        }
    }

    public boolean commandChooseClass(IActiveCharacter character, ClassDefinition configClass) {
        if (character.isStub()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED));
            return false;
        }

        ActionResult result = characterService.canGainClass(character, configClass);
        if (result.isOk()) {
            characterService.addNewClass(character, configClass);
        } else {
            character.sendMessage(result.getMessage());
        }
        return true;
    }

    public void commandCreateCharacter(UUID uuid, String name, Consumer<ActionResult> actionResultConsumer) {
        CompletableFuture.runAsync(() -> {
            int i = characterService.canCreateNewCharacter(uuid, name);
            if (i == 1) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.REACHED_CHARACTER_LIMIT)));
            } else if (i == 2) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.CHARACTER_EXISTS)));
            } else if (i == 0) {
                CharacterBase characterBase = characterService.createCharacterBase(name, uuid);

                characterService.create(characterBase);

                String text = localizationService.translate(LocalizationKeys.CHARACTER_CREATED, Arg.arg("name", characterBase.getName()));

                actionResultConsumer.accept(ActionResult.ok(text));

                IActiveCharacter character = characterService.getCharacter(uuid);
                Gui.sendListOfCharacters(character, characterBase);
            }
        }, Rpg.get().getAsyncExecutor());
    }

    public void commandSwitchCharacter(IActiveCharacter current, String nameNext, Consumer<Runnable> syncCallback) {
        if (current != null && current.getName().equalsIgnoreCase(nameNext)) {
            current.sendMessage(localizationService.translate(LocalizationKeys.ALREADY_CUURENT_CHARACTER));
            return;
        }
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
        }, Rpg.get().getAsyncExecutor());
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
