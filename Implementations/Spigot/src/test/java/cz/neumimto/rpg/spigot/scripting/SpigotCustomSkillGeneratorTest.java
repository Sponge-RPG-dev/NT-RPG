package cz.neumimto.rpg.spigot.scripting;

import cz.neumimto.rpg.spigot.scripting.SpigotCustomSkillGenerator.SpigotParticleMacro;
import org.junit.jupiter.api.Test;

public class SpigotCustomSkillGeneratorTest {


    @Test
    public void testMacro() {
        SpigotParticleMacro spigotParticleMacro = new SpigotParticleMacro("circle(radius=5,effect=cloud");
        System.out.println(spigotParticleMacro.toString());
    }
}
