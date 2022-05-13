package cz.neumimto.rpg.common.entity;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.configuration.Attributes;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.properties.Property;
import cz.neumimto.rpg.common.utils.Console;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

import static cz.neumimto.rpg.common.logging.Log.info;

@Singleton
public class PropertyService {

    public static final double WALKING_SPEED = 0.1d;

    public static int LAST_ID = 0;
    public static final Supplier<Integer> getAndIncrement = () -> LAST_ID++;

    protected float[] maxValues;
    @Inject
    private ItemService itemService;
    @Inject
    private AssetService assetService;
    private Map<String, Integer> idMap = new HashMap<>();
    private Map<Integer, String> nameMap = new HashMap<>();
    private Map<Integer, Float> defaults = new HashMap<>();
    private Map<String, AttributeConfig> attributeMap = new HashMap<>();

    public int getLastId() {
        return LAST_ID;
    }

    public void registerProperty(String name, int id) {
        info("A new property " + name + "; assigned id: " + id, Rpg.get().getPluginConfig().DEBUG);
        idMap.put(name, id);
        nameMap.put(id, name);
    }

    public int getIdByName(String name) {
        return idMap.get(name);
    }

    public boolean exists(String property) {
        return idMap.containsKey(property);
    }

    public String getNameById(Integer id) {
        return nameMap.get(id);
    }

    public void registerDefaultValue(int id, float def) {
        defaults.put(id, def);
    }

    public float getDefaultValue(int id) {
        return defaults.get(id);
    }

    public Map<Integer, Float> getDefaults() {
        return defaults;
    }

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

    public float getDefault(Integer key) {
        Float f = defaults.get(key);
        if (f == null) {
            return 0;
        }
        return f;
    }

    public float getMaxPropertyValue(int index) {
        return maxValues[index];
    }

    public Collection<String> getAllProperties() {
        return nameMap.values();
    }

    public void overrideMaxPropertyValue(String s, Float aFloat) {
        if (!nameMap.containsValue(s)) {
            info("Attempt to override default value for a property \"" + s + "\". But such property does not exists yet. THe property will be created");
            registerProperty(s, getAndIncrement.get());
        }
        defaults.put(getIdByName(s), aFloat);
        info(" Property \"" + s + "\" default value is now " + aFloat + ". This change wont affect already joined players!");
    }

    public void loadMaximalServerPropertyValues() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "max_server_property_values.properties");

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

    public Optional<AttributeConfig> getAttributeById(String attribute) {
        return Optional.ofNullable(getAttributes().get(attribute.toLowerCase()));
    }

    public Map<String, AttributeConfig> getAttributes() {
        return attributeMap;
    }

    public Optional<AttributeConfig> getById(String id) {
        return getAttributeById(id);
    }

    public Collection<AttributeConfig> getAll() {
        return getAttributes().values();
    }

    public void reLoadAttributes() {
        Path attributesPath = Paths.get(Rpg.get().getWorkingDirectory() + "/Attributes.conf");
        try (FileConfig fc = FileConfig.of(attributesPath)) {
            fc.load();
            Attributes attributes = new ObjectConverter().toObject(fc, Attributes::new);
            attributes.getAttributes().forEach(a -> attributeMap.put(a.getId(), a));
            itemService.registerItemAttributes(Rpg.get().getPropertyService().getAttributes().values());
        }
    }

    public void load() {
        StringBuilder s = new StringBuilder();
        List<String> l = new ArrayList<>(idMap.keySet());
        info(" - found " + l.size() + " Properties", Rpg.get().getPluginConfig().DEBUG);
        l.sort(Collator.getInstance());
        for (String s1 : l) {
            s.append(s1).append('\n');
        }
        try {
            Path propertiesDump = Paths.get(Rpg.get().getWorkingDirectory() + File.separator + "properties_dump.info");
            Files.write(propertiesDump, s.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path attributeConf = Paths.get(Rpg.get().getWorkingDirectory() + "/Attributes.conf");
        File f = attributeConf.toFile();
        if (!f.exists()) {
            assetService.copyToFile("Attributes.conf", attributeConf);
        }

        reLoadAttributes();
        loadMaximalServerPropertyValues();
    }

    public void reload() {
        attributeMap.clear();
        load();
    }
}
