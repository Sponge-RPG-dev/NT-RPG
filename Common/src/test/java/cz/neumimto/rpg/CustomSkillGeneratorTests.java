package cz.neumimto.rpg;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.scripting.mechanics.ApplyEffect;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith({CharactersExtension.class, GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class CustomSkillGeneratorTests {

    @Inject
    private CustomSkillGenerator customSkillGenerator;

    @Inject
    private NearbyEnemies nearbyEnemies;

    @Inject
    private ApplyEffect applyEffect;

    @Inject
    private DamageCheck damageCheck;

    @Inject
    private Injector injector;

    private static AtomicBoolean atomicBoolean;

    @BeforeEach
    public void before() {
        atomicBoolean = new AtomicBoolean(false);
    }

    @Test
    public void test() {
        URL resource = getClass().getClassLoader().getResource("skillgen/test01.conf");
        ScriptSkillModel model;
        try (FileConfig fileConfig = FileConfig.of(new File(resource.getPath()))) {
            fileConfig.load();

            model = new ObjectConverter().toObject(fileConfig, ScriptSkillModel::new);
            injector.getInstance(DamageMechanic.class);

            Class<ActiveSkill> i= (Class<ActiveSkill>) customSkillGenerator.generate(model, ClassLoader.getSystemClassLoader());

            PlayerSkillContext context = new PlayerSkillContext(null, null, null);

            injector.getInstance(i).cast(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test2() {
        URL resource = getClass().getClassLoader().getResource("skillgen/test03.conf");
        ScriptSkillModel model;
        try (FileConfig fileConfig = FileConfig.of(new File(resource.getPath()))) {
            fileConfig.load();

            model = new ObjectConverter().toObject(fileConfig, ScriptSkillModel::new);
            injector.getInstance(DamageMechanic.class);

            Class<ActiveSkill> i= (Class<ActiveSkill>) customSkillGenerator.generate(model, ClassLoader.getSystemClassLoader());

            PlayerSkillContext context = new PlayerSkillContext(null, null, null);

            injector.getInstance(i).cast(null, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Singleton
    public static class TestMechanic {

        public void doAction(IActiveCharacter character, float value) {

        }
    }



    public static class DamageCheck {

        public boolean check(IActiveCharacter caster, IEntity entity) {
            return true;
        }

    }

    public static class NearbyEnemies {

        public List<IEntity> getTargets(IActiveCharacter character, float range) {
            return Collections.emptyList();
        }
    }

}
