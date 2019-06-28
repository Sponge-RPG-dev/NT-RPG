package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

@Singleton
public class CharacterCommandFacade {

    @Inject
    private ICharacterService<IActiveCharacter> characterService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private PermissionService permissionService;

    public void commandAddAttribute(IActiveCharacter character, AttributeConfig iCharacterAttribute, int amount) {
        characterService.addAttribute(character, iCharacterAttribute, amount);
        characterService.putInSaveQueue(character.getCharacterBase());
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
            LocalizationService localizationService = Rpg.get().getLocalizationService();
            if (i == 1) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.REACHED_CHARACTER_LIMIT)));
            } else if (i == 2) {
                actionResultConsumer.accept(ActionResult.withErrorMessage(localizationService.translate(LocalizationKeys.CHARACTER_EXISTS)));
            } else if (i == 0) {
                JPACharacterBase characterBase = new JPACharacterBase();
                characterBase.setUuid(uuid);
                characterBase.setName(name);
                characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
                characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);

                characterService.createAndUpdate(characterBase);

                String text = localizationService.translate(LocalizationKeys.CHARACTER_CREATED, Arg.arg("name", characterBase.getName()));

                actionResultConsumer.accept(ActionResult.ok(text));

                Gui.sendListOfCharacters(characterService.getCharacter(uuid), characterBase);
            }
        }, Rpg.get().getAsyncExecutor());
    }
}
