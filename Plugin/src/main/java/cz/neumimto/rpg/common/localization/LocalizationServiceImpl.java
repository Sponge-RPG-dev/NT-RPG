package cz.neumimto.rpg.common.localization;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class LocalizationServiceImpl implements LocalizationService {

    private Map<String, String> map = new HashMap<>();
    private Map<String, List<String>> mapMultiLine = new HashMap<>();

    @Override
    public void addTranslationKey(String key, String translation) {
        map.put(key, translation);
    }

    //todo benchamrk this
    @Override
    public String translate(String key, Arg args) {
        String s = map.get(key);
        if (s == null) {
            return key + " | " + args.getParams().keySet().stream().collect(Collectors.joining(","));
        }
        Map<String, Object> params = args.getParams();
        return StringUtils.replaceEach(s, params.keySet().stream().toArray(String[]::new), params.values().stream().map(Object::toString).toArray(String[]::new));
    }

    @Override
    public String translate(String message, String singleKey, String singleArg) {
        String s = map.get(message);
        if (s == null) {
            return message + " | " + singleKey;
        }
        return StringUtils.replace(s, singleKey, singleArg);
    }

    @Override
    public String translate(String staticMessage) {
        String s =  map.get(staticMessage);
        if (s == null) {
            return staticMessage;
        }
        return s;
    }

    @Override
    public List<String> translateMultiline(String s) {
        List<String> s1 = mapMultiLine.get(s);
        if (s1 == null) {
            return Arrays.asList(s);
        }
        return s1;
    }

    @Override
    public void loadResourceBundle(String resourceBundle, Locale locale, URLClassLoader localizationsClassLoader) {
        ResourceBundle translations = ResourceBundle.getBundle(resourceBundle, locale, localizationsClassLoader);
        Enumeration<String> keys = translations.getKeys();
        while (keys.hasMoreElements()) {
            String s = keys.nextElement();
            String string = translations.getString(s);
            if (s.contains("multiline")) {
                String[] split = string.split(":n");
                List<String> strings = Arrays.asList(split);
                mapMultiLine.put(s, strings);
            } else {
                map.put(s, string);
            }
        }
    }

}
