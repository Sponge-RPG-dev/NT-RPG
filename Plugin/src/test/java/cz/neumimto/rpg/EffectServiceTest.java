package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.CommonEffectTypes;
import cz.neumimto.rpg.api.effects.EffectType;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.effects.TestEffectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EffectServiceTest {

    @Test
    void registerEffectTypes() {
        IEffectService e = new TestEffectService();
        Optional<EffectType> type = e.getEffectType(CommonEffectTypes.SILENCE.toString());
        Assertions.assertTrue(type.isPresent());
    }
}