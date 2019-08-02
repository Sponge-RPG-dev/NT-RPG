package cz.neumimto.rpg.common.entity;

import com.google.inject.Singleton;

import java.nio.file.Path;

@Singleton
public class TestPropertyService extends PropertyService {

    public TestPropertyService() {
        LAST_ID = 100;
        maxValues = new float[LAST_ID];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = 500;
        }
    }

    @Override
    public void init(Path attributeConf, Path propertiesDump) {

    }

    @Override
    public void reLoadAttributes(Path attributeFilePath) {

    }

}