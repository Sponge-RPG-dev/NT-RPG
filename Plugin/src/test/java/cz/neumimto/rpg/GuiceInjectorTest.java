package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class GuiceInjectorTest {
    @Inject
    private ICharacterService<IActiveCharacter> characterService;

    @Inject
    private SpongeCharacterServise spongeCharacterServise;

    @Inject
    private ICharacterService<? extends IActiveCharacter> characterService2;

    @Inject
    private ICharacterService<? super IActiveCharacter> characterService3;

    @Test
    public void test() {
        Assertions.assertNotNull(characterService);
        Assertions.assertNotNull(spongeCharacterServise);
        Assertions.assertNotNull(characterService2);
        Assertions.assertNotNull(characterService3);

        Assertions.assertSame(characterService, spongeCharacterServise);
        Assertions.assertSame(spongeCharacterServise, characterService2);
        Assertions.assertSame(characterService2, characterService3);
    }
}
