package cz.neumimto.rpg.common.localization;

import cz.neumimto.rpg.common.utils.StringUtils;

import javax.inject.Singleton;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class LocalizationServiceImpl implements LocalizationService {

    private Map<String, String> map = new HashMap<>();
    private Map<String, List<String>> mapMultiLine = new HashMap<>();

    @Override
    public void addTranslationKey(String key, String translation) {
        if (key.contains(".multiline.")) {
            addMultilineTranslationkey(key, translation);
        } else {
            map.put(key, translation);
        }
    }

    @Override
    public void addMultilineTranslationkey(String key, String lines) {
        List<String> collect = Stream.of(lines.split(":n")).collect(Collectors.toList());
        mapMultiLine.put(key, collect);
    }

    //todo benchamrk this
    @Override
    public String translate(String key, Arg args) {
        String s = map.get(key);
        if (s == null) {
            return key + " | " + args.getParams().keySet().stream().collect(Collectors.joining(","));
        }
        Map<String, Object> params = args.getParams();
        return StringUtils.replaceEach(s, params.keySet().toArray(new String[0]), params.values().stream().map(Object::toString).toArray(String[]::new));
    }

    @Override
    public String translate(String message, String singleKey, String singleArg) {
        String s = map.get(message);
        if (s == null) {
            return message + " | " + singleKey;
        }
        return StringUtils.replace(s, Arg.START_TAG + singleKey + Arg.END_TAG, singleArg);
    }

    @Override
    public String translate(String staticMessage) {
        String s = map.get(staticMessage);
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
    public List<String> translateRaw(List<String> template, Arg arg) {
        List<String> list = new ArrayList<>();
        for (String s : template) {
            Map<String, Object> params = arg.getParams();
            list.add(StringUtils.replaceEach(s, params.keySet().toArray(new String[0]), params.values().stream().map(Object::toString).toArray(String[]::new)));
        }
        return list;
    }

    @Override
    public void loadResourceBundle(String resourceBundle, Locale locale, URLClassLoader localizationsClassLoader) {
        ResourceBundle translations = ResourceBundle.getBundle(resourceBundle, locale, localizationsClassLoader);
        Enumeration<String> keys = translations.getKeys();
        while (keys.hasMoreElements()) {
            String s = keys.nextElement();
            String string = translations.getString(s);
            map.put(s, string);
        }
    }

}
