package cz.neumimto.rpg.common.entity.configuration;

import javax.inject.Singleton;

import static org.junit.jupiter.api.Assertions.*;

@Singleton
public class TestMobSettingsDao extends MobSettingsDao {

    @Override
    protected RootMobConfig createDefaults(String s) {
        return null;
    }
}