package cz.neumimto.rpg.common.resources;

import com.electronwill.nightconfig.core.conversion.Path;

public class ResourceDefinition {

    @Path("Name")
    public String name;

    @Path("Type")
    public String type;

    @Path("CombatRegen")
    public boolean combatRegen;

    @Path("RegenRate")
    public long regenRate;

    public static class Types {
        public static String HEALTH = "health";
        public static String STAMINA = "stamina";
        public static String REGENERATING = "regenerating";
        public static String PASSIVE = "passive";
    }
}
