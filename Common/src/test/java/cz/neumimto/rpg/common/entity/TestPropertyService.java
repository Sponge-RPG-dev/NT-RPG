package cz.neumimto.rpg.common.entity;

import com.google.inject.Singleton;

@Singleton
public class TestPropertyService extends PropertyServiceImpl {

    public TestPropertyService() {
        LAST_ID = 100;
        maxValues = new float[LAST_ID];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = 500;
        }
    }

    @Override
    public void load() {

    }

    @Override
    public void reLoadAttributes() {

    }

}