package cz.neumimto.rpg.api.localization;

import cz.neumimto.core.localization.Arg;

import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;

public interface LocalizationService {

    void addTranslationKey(String key, String translation);

    String translate(String key, Arg arg);

    String translate(String message, String singleKey, String singleArg);

    String translate(String staticMessage);

    List<String> translateMultiline(String s);

    void loadTranslations(Class<?> clazz);

    void loadResourceBundle(String resourceBundle, Locale locale, URLClassLoader localizationsClassLoader);
}
