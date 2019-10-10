package cz.neumimto.rpg.common.entity.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestMobConfigLoading {

    @Test
    public void test() {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Mobs.conf").getFile());
        RootMobConfig rootMobConfig = new MobSettingsDao() {
            @Override
            protected RootMobConfig createDefaults() {
                return null;
            }
        }.loadFile(file.toPath());
        Assertions.assertTrue(rootMobConfig.getDimmensions().size() > 0);

    }
}
