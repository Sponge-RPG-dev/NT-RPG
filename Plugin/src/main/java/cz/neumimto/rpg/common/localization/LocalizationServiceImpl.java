package cz.neumimto.rpg.common.localization;

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

    @Override
    public String translate(String key, String[] keys, String[] args) {
        String s = map.get(key);
        return StringUtils.replaceEachRepeatedly(s, keys, args);
    }
}
