package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.CommonEffectTypes;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.EffectType;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
class EffectServiceTest {

    @Test
    void registerEffectTypes() {
        EffectService e = new TestEffectService();
        Optional<EffectType> type = e.getEffectType(CommonEffectTypes.SILENCE.toString());
        Assertions.assertTrue(type.isPresent());
    }
}