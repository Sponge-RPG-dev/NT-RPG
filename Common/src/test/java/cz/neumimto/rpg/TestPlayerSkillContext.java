package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
public class TestPlayerSkillContext {

    private IActiveCharacter character;

    @BeforeEach
    public void before(@CharactersExtension.Stage(READY) IActiveCharacter character) {
        this.character = character;
    }

    @Test
    public void test_skill_upg() {
        
    }
}
