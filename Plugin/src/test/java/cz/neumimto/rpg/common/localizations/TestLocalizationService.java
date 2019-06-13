package cz.neumimto.rpg.common.localizations;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;

import java.util.List;

public class TestLocalizationService extends LocalizationServiceImpl {

    @Override
    public String translate(String staticMessage) {
        return staticMessage;
    }

    @Override
    public String translate(String key, Arg args) {
        return key;
    }
}
