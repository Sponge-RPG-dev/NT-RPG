package cz.neumimto.rpg.api.localization;

import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;

public interface LocalizationService {

    void addTranslationKey(String key, String translation);

    void addMultilineTranslationkey(String key, String lines);

    String translate(String key, Arg arg);

    String translate(String message, String singleKey, String singleArg);

    String translate(String staticMessage);

    List<String> translateMultiline(String s);

    List<String> translateRaw(List<String> template, Arg arg);

    void loadResourceBundle(String resourceBundle, Locale locale, URLClassLoader localizationsClassLoader);

}
