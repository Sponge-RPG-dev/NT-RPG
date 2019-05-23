package cz.neumimto.rpg.api.localization;

import cz.neumimto.core.localization.Arg;

public interface LocalizationService {

    void addTranslationKey(String key, String translation);

    String translate(String key, Arg arg);

    String translate(String message, String singleKey, String singleArg);
}
