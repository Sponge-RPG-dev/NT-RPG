package cz.neumimto.rpg.common.entity;

import cz.neumimto.config.blackjack.and.hookers.NotSoStupidObjectMapper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.Attributes;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.properties.Property;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.common.assets.AssetService;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

import static cz.neumimto.rpg.api.logging.Log.info;

@Singleton
public class PropertyService implements IPropertyService {

    public static final double WALKING_SPEED = 0.1d;

    public static int LAST_ID = 0;
    public static final Supplier<Integer> getAndIncrement = () -> {
        int t = new Integer(LAST_ID);
        LAST_ID++;
        return t;
    };

    @Inject
    private ItemService itemService;

    @Inject
    private AssetService assetService;

    private Map<String, Integer> idMap = new HashMap<>();

    private Map<Integer, String> nameMap = new HashMap<>();

    private Map<Integer, Float> defaults = new HashMap<>();

    private Set<Integer> damageRecalc = new HashSet<>();

    private Map<String, AttributeConfig> attributeMap = new HashMap<>();

    protected float[] maxValues;

    @Override
    public int getLastId() {
        return LAST_ID;
    }

    @Override
    public void registerProperty(String name, int id) {
        info("A new property " + name + "; assigned id: " + id, Rpg.get().getPluginConfig().DEBUG);
        idMap.put(name, id);
        nameMap.put(id, name);
    }

    @Override
    public int getIdByName(String name) {
        return idMap.get(name);
    }

    @Override
    public boolean exists(String property) {
        return idMap.containsKey(property);
    }

    @Override
    public String getNameById(Integer id) {
        return nameMap.get(id);
    }

    @Override
    public void registerDefaultValue(int id, float def) {
        defaults.put(id, def);
    }

    @Override
    public float getDefaultValue(int id) {
        return defaults.get(id);
    }

    @Override
    public Map<Integer, Float> getDefaults() {
        return defaults;
    }

    @Override
    public void processContainer(Class<?> container) {
        int value;
        for (Field f : container.getDeclaredFields()) {
            if (f.isAnnotationPresent(Property.class)) {
                Property p = f.getAnnotation(Property.class);
                value = getAndIncrement.get();
                try {
                    f.setInt(null, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                if (!p.name().trim().equalsIgnoreCase("")) {
                    registerProperty(p.name(), value);
                }
                if (p.default_() != 0f) {
                    registerDefaultValue(value, p.default_());
                }
            }
        }
    }

    @Override
    public float getDefault(Integer key) {
        Float f = defaults.get(key);
        if (f == null) {
            return 0;
        }
        return f;
    }

    @Override
    public float getMaxPropertyValue(int index) {
        return maxValues[index];
    }

    @Override
    public Collection<String> getAllProperties() {
        return nameMap.values();
    }

    @Override
    public void overrideMaxPropertyValue(String s, Float aFloat) {
        if (!nameMap.containsValue(s)) {
            info("Attempt to override default value for a property \"" + s + "\". But such property does not exists yet. THe property will be created");
            registerProperty(s, getAndIncrement.get());
        }
        defaults.put(getIdByName(s), aFloat);
        info(" Property \"" + s + "\" default value is now " + aFloat + ". This change wont affect already joined players!");
    }

    @Override
    public boolean updatingRequiresDamageRecalc(int propertyId) {
        return damageRecalc.contains(propertyId);
    }

    @Override
    public void addPropertyToRequiresDamageRecalc(int i) {
        damageRecalc.add(i);
    }

    @Override
    public void loadMaximalServerPropertyValues(Path path) {
        maxValues = new float[LAST_ID];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = Float.MAX_VALUE;
        }
        File file = path.toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Set<String> missing = new HashSet<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            for (String s : idMap.keySet()) {
                Object o = properties.get(s);
                if (o == null) {
                    missing.add(s);
                    info("Missing property \"" + Console.GREEN + s + Console.RESET + "\" in the file max_server_property_values.properties");
                    info(" - Appending the file and setting its default value to 1000; You might want to reconfigure that file.");
                    maxValues[getIdByName(s)] = 1000f;
                } else {
                    maxValues[getIdByName(s)] = Float.parseFloat(o.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!missing.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String a : missing) {
                    writer.write(a + "=1000" + System.lineSeparator());
                }
            } catch (IOException e) {
                Log.error("Could not append file max_server_property_values.properties", e);
            }
        }
    }

    @Override
    public Optional<AttributeConfig> getAttributeById(String attribute) {
        return Optional.ofNullable(getAttributes().get(attribute));
    }

    @Override
    public Map<String, AttributeConfig> getAttributes() {
        return attributeMap;
    }

    public Optional<AttributeConfig> getById(String id) {
        return getAttributeById(id);
    }

    public Collection<AttributeConfig> getAll() {
        return getAttributes().values();
    }

    @Override
    public void reLoadAttributes(Path attributeFilePath) {
        try {
            ObjectMapper<Attributes> mapper = NotSoStupidObjectMapper.forClass(Attributes.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(attributeFilePath).build();
            Attributes attributes = mapper.bind(new Attributes()).populate(hcl.load());
            attributes.getAttributes().forEach(a -> attributeMap.put(a.getId(), a));
            itemService.registerItemAttributes(Rpg.get().getPropertyService().getAttributes().values());
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Path attributeConf, Path propertiesDump) {
        StringBuilder s = new StringBuilder();
        List<String> l = new ArrayList<>(idMap.keySet());
        info(" - found " + l.size() + " Properties", Rpg.get().getPluginConfig().DEBUG);
        l.sort(Collator.getInstance());
        for (String s1 : l) {
            s.append(s1).append('\t');
        }
        try {
            Files.write(propertiesDump, s.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = attributeConf.toFile();
        if (!f.exists()) {
            assetService.copyToFile("Attributes.conf", attributeConf);
        }
    }
}
