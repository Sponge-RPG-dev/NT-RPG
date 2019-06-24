package cz.neumimto.rpg;

import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class LocalizationServiceTest {

    @Inject
    private LocalizationService localizationService;

    @Test
    public void testSingleStringTranslation() {
        localizationService.addTranslationKey("test.singlestr","translated text");
        Assertions.assertEquals(localizationService.translate("test.singlestr"), "translated text");
    }

    @Test
    public void testSingleStringTranslation_SingleArg() {
        localizationService.addTranslationKey("test.singlestr","translated text {{param}}");
        String translate = localizationService.translate("test.singlestr", Arg.arg("param", "abc"));
        Assertions.assertEquals(translate, "translated text abc");
    }


    @Test
    public void testSingleStringTranslation_SingleArg02() {
        localizationService.addTranslationKey("test.singlestr","translated text {{param}}");
        String translate = localizationService.translate("test.singlestr", "param", "abc");
        Assertions.assertEquals(translate, "translated text abc");
    }

}
