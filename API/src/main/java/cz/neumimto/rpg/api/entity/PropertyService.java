package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.configuration.AttributeConfig;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface PropertyService {

    void load();

    void reload();

    void reLoadAttributes();

    int getIdByName(String name);

    boolean exists(String property);

    String getNameById(Integer id);

    void registerDefaultValue(int id, float def);

    float getDefaultValue(int id);

    Map<Integer, Float> getDefaults();

    void processContainer(Class<?> container);

    float getDefault(Integer key);

    float getMaxPropertyValue(int index);

    Collection<String> getAllProperties();

    void overrideMaxPropertyValue(String s, Float aFloat);

    boolean updatingRequiresDamageRecalc(int propertyId);

    void addPropertyToRequiresDamageRecalc(int i);

    void loadMaximalServerPropertyValues();

    Optional<AttributeConfig> getAttributeById(String attribute);

    Map<String, AttributeConfig> getAttributes();

    int getLastId();

    void registerProperty(String property, int val);
}
