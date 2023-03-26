package cz.neumimto.rpg.common.configuration;

import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.typesafe.config.Optional;
import cz.neumimto.rpg.common.utils.DebugLevel;

import java.util.*;

/**
 * Created by NeumimTo on 26.12.2014.
 */
public class PluginConfig {

    @Path("COMBAT_TIME")
    public long COMBAT_TIME = 20000L;

    @Path("REMOVE_PLAYERDATA_AFTER_PERMABAN")
    public boolean REMOVE_PLAYERDATA_AFTER_PERMABAN = false;

    @Path("DEBUG")
    public DebugLevel DEBUG = DebugLevel.NONE;

    @Path("PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR")
    public boolean PLAYER_AUTO_CHOOSE_LAST_PLAYED_CHAR = true;

    @Path("SKILLGAIN_MESSAGES_AFTER_LOGIN")
    public boolean SKILLGAIN_MESSAGES_AFTER_LOGIN = true;

    @Path("PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE")
    public boolean PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE = true;

    @Path("PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE")
    public boolean PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE = true;

    @Path("PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE")
    public boolean PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE = true;

    @Path("ATTRIBUTEPOINTS_ON_START")
    public int ATTRIBUTEPOINTS_ON_START = 1;

    @Path("PLAYER_MAX_CHARS")
    public int PLAYER_MAX_CHARS = 5;

    @Path("ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS")
    public boolean ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS = true;

    @Path("PARTY_EXPERIENCE_MULTIPLIER")
    public double PARTY_EXPERIENCE_MULTIPLIER = 2;

    @Path("PARTY_EXPERIENCE_SHARE_DISTANCE")
    public double PARTY_EXPERIENCE_SHARE_DISTANCE = 25;

    @Path("MAX_PARTY_SIZE")
    public double MAX_PARTY_SIZE = -1;

    @Path("PLAYER_CHOOSED_SKILLTREE_SPECIALIZATION_GLOBAL_MESSAGE")
    @Optional
    public boolean PLAYER_CHOOSED_SKILLTREE_SPECIALIZATION_GLOBAL_MESSAGE;

    @Path("PATH_NODES_SEALED")
    public boolean PATH_NODES_SEALED = true;

    @Path("SHIFT_CANCELS_COMBO")
    public boolean SHIFT_CANCELS_COMBO = false;

    @Path("ENABLED_Q")
    public boolean ENABLED_Q;

    @Path("ENABLED_E")
    public boolean ENABLED_E;

    @Path("CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS")
    public long CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS = 1250;

    @Path("CREATE_FIRST_CHAR_AFTER_LOGIN")
    public boolean CREATE_FIRST_CHAR_AFTER_LOGIN = true;

    @Path("LOCALE")
    public String LOCALE = "en";

    @Path("MAX_CLICK_COMBO_LENGTH")
    public int MAX_CLICK_COMBO_LENGTH = 6;

    @Path("PRIMARY_CLASS_TYPE")
    public String PRIMARY_CLASS_TYPE = "Primary";

    @Path("RESPECT_CLASS_SELECTION_ORDER")
    public boolean RESPECT_CLASS_SELECTION_ORDER = false;

    @Path("CLASS_TYPES")
    @Conversion(ClassTypesDeserializer.class)
    public Map<String, ClassTypeDefinition> CLASS_TYPES = new LinkedHashMap<String, ClassTypeDefinition>() {{
        put("Race", new ClassTypeDefinition("GREEN", "DARK_GREEN", "GREEN", false, 1, 11111));
        put("Primary", new ClassTypeDefinition("YELLOW", "GOLD", "YELLOW", true, 2, 11112));
        put("Profession", new ClassTypeDefinition("GRAY", "BLACK", "GRAY", true, 3, 11113));
    }};

    @Path("RESPEC_ATTRIBUTES")
    public boolean RESPEC_ATTRIBUTES = false;

    @Path("DISABLED_WORLDS")
    @Conversion(SetToListConverter.class)
    public Set<String> DISABLED_WORLDS = new HashSet<>();

    @Path("ITEM_COOLDOWNS")
    public Boolean ITEM_COOLDOWNS = Boolean.TRUE;

    @PreserveNotNull
    @Path("SPELLBOOK_ROTATION_COOLDOWN")
    public long SPELLBOOK_COOLDOWN = 1000L;

    @Path("SKILL_SETTINGS_ICONS")
    @Conversion(SSIConverter.class)
    public Map<String, ItemString> SKILL_SETTINGS_ICONS = new HashMap<String, ItemString>() {{
        put("cooldown", ItemString.parse("minecraft:paper;model=10"));
        put("mana", ItemString.parse("minecraft:paper;model=11"));
        put("damage", ItemString.parse("minecraft:paper;model=12"));
        put("duration", ItemString.parse("minecraft:paper;model=13"));
        put("period", ItemString.parse("minecraft:paper;model=14"));
    }};

    @Path("DISABLED_HOOKS")
    public List<String> DISABLED_HOOKS = new ArrayList<>();

    @Path("FEATURES")
    public List<String> FEATURES = new ArrayList<>() {{
        add("skillcast_holograms");
    }};

    private static class SetToListConverter implements Converter<Set, List> {
        @Override
        public Set convertToField(List value) {
            HashSet hashSet = new HashSet();
            if (value != null) {
                hashSet.addAll(value);
            }
            return hashSet;
        }

        @Override
        public List convertFromField(Set value) {
            List list = new ArrayList();
            if (value != null) {
                list.addAll(value);
            }
            return list;
        }
    }

    private static class SSIConverter implements Converter<Map<String, ItemString>, List<String>> {

        @Override
        public Map<String, ItemString> convertToField(List<String> value) {
            Map<String, ItemString> map = new HashMap<>();

            for (String s : value) {
                String[] split = s.split(";");

                String k = split[1] + ";" + split[2];

                map.put(split[0], ItemString.parse(k));
            }


            return map;
        }

        @Override
        public List<String> convertFromField(Map<String, ItemString> value) {
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, ItemString> entry : value.entrySet()) {
                list.add(entry.getKey() + ";" + entry.getValue().itemId + ";model=" + entry.getValue().variant);
            }
            return list;
        }
    }

}
