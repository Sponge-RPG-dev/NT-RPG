package cz.neumimto.rpg.api.localization;

public interface LocalizationService {

    void addTranslationKey(String key, String translation);

    String translate(String key, String[] keys, String[] args);

    String translate(String message, String singleKey, String singleArg);
}
