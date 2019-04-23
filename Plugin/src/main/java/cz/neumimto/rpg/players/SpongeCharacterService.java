package cz.neumimto.rpg.players;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.ActionResult;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.events.PlayerDataPreloadComplete;
import cz.neumimto.rpg.events.character.*;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SpongeCharacterService extends CharacterService {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    @Override
    protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {
        game.getScheduler().createTaskBuilder().name("Callback-PlayerDataLoad" + id).execute(() -> {
            PlayerDataPreloadComplete event = new PlayerDataPreloadComplete(id, playerChars);
            game.getEventManager().post(event);

            Optional<Player> popt = game.getServer().getPlayer(event.getPlayer());
            if (popt.isPresent()) {
                finalizePlayerDataPreloadStage(id, character, event, popt.get());
            } else {
                playerDataPreloadStagePlayerNotReady(id, character);
            }
        }).submit(plugin);
    }

    public void updateWeaponRestrictions(IActiveCharacter character) {
        Map weapons = character.getAllowedWeapons();

        CharacterWeaponUpdateEvent event = new CharacterWeaponUpdateEvent(character, weapons);
        game.getEventManager().post(event);
    }

    @Override
    public void updateArmorRestrictions(IActiveCharacter character) {
        Set allowedArmor = character.getAllowedArmor();

        EventCharacterArmorPostUpdate event = new EventCharacterArmorPostUpdate(character, allowedArmor);
        game.getEventManager().post(event);

    }


    @Override
    public void initActiveCharacter(IActiveCharacter character) {
        super.initActiveCharacter(character);

        CharacterInitializedEvent event = new CharacterInitializedEvent(character);
        game.getEventManager().post(event);
    }

    @Override
    public ActionResult canUpgradeSkill(IActiveCharacter character, ClassDefinition classDef, ISkill skill) {
        ActionResult actionResult =  super.canUpgradeSkill(character, classDef, skill);
        if (actionResult.isOk()) {
            CharacterSkillUpgradeEvent event = new CharacterSkillUpgradeEvent(character, skill);
            if (game.getEventManager().post(event)) {
                return ActionResult.withErrorMessage(event.getFailedMessage());
            }
        }
        return actionResult;
    }

    @Override
    public ActionResult canLearnSkill(IActiveCharacter character, ClassDefinition classDef, ISkill skill) {
        ActionResult actionResult = super.canLearnSkill(character, classDef, skill);

        if (actionResult.isOk()) {
            CharacterSkillLearnAttemptEvent event = new CharacterSkillLearnAttemptEvent(character, skill);
            game.getEventManager().post(event);
            if (event.isCancelled()) {
                return ActionResult.withErrorMessage(event.getFailedMessage());
            }
        }

        return actionResult;
    }

    @Override
    public ActionResult canRefundSkill(IActiveCharacter character, ClassDefinition classDefinition, ISkill skill) {
        ActionResult actionResult = super.canRefundSkill(character, classDefinition, skill);

        if (actionResult.isOk()) {
            CharacterSkillRefundAttemptEvent event = new CharacterSkillRefundAttemptEvent(character, skill);
            game.getEventManager().post(event);
            if (event.isCancelled()) {
                return ActionResult.withErrorMessage(Localizations.UNABLE_TO_REFUND_SKILL.toText());
            }
        }
        return actionResult;
    }

    @Override
    public int addAttribute(IActiveCharacter character, Attribute attribute, int i) {
        CharacterAttributeChange event = new CharacterAttributeChange(character, i);
        game.getEventManager().post(event);
        if (event.isCancelled()) {
            return 1;
        }
        return super.addAttribute(character, attribute, i);
    }
}
