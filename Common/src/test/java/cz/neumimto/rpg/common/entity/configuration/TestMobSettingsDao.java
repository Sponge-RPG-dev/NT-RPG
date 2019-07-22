package cz.neumimto.rpg.common.entity.configuration;

import javax.inject.Singleton;

@Singleton
public class TestMobSettingsDao extends MobSettingsDao {


    protected RootMobConfig createDefaults() {
        return null;
    }


}