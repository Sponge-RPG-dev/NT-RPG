package cz.neumimto.rpg.api.skills.mods;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;

public class ResultNotificationSkillExecutor extends SkillExecutorCallback {

    public static final ResultNotificationSkillExecutor INSTANCE;

    static {
        INSTANCE = new ResultNotificationSkillExecutor();
    }


    private ResultNotificationSkillExecutor() {

    }

    @Override
    public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        switch (skillResult.getResult()) {
            case ON_COOLDOWN:
                break;
            case NO_MANA:
                character.sendMessage(localizationService.translate(LocalizationKeys.NO_MANA));
                break;
            case NO_HP:
                character.sendMessage(localizationService.translate(LocalizationKeys.NO_HP));
                break;
            case CASTER_SILENCED:
                character.sendMessage(localizationService.translate(LocalizationKeys.PLAYER_IS_SILENCED));
                break;
            case NO_TARGET:
                character.sendMessage(localizationService.translate(LocalizationKeys.NO_TARGET));
                break;
            case OK:
                character.sendNotification(info.getSkillData().getSkillName());
                break;
            default:
                character.sendNotification(localizationService.translate(LocalizationKeys.CASTING_FAILED));
        }
    }
}
