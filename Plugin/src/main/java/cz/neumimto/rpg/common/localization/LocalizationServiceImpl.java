package cz.neumimto.rpg.common.localization;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LocalizationServiceImpl implements LocalizationService {

    private Map<String, String> map = new HashMap<>();

    @Override
    public void addTranslationKey(String key, String translation) {
        map.put(key, translation);
    }

    //todo benchamrk this
    @Override
    public String translate(String key, Arg args) {
        String s = map.get(key);
        Map<String, Object> params = args.getParams();
        return StringUtils.replaceEach(s, params.keySet().stream().toArray(String[]::new), params.values().stream().map(Object::toString).toArray(String[]::new));
    }

    @Override
    public String translate(String message, String singleKey, String singleArg) {
        String s = map.get(message);
        return StringUtils.replace(s, singleKey, singleArg);
    }
}
