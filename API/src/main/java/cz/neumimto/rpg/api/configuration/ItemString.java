package cz.neumimto.rpg.api.configuration;

import cz.neumimto.rpg.api.logging.Log;

public final class ItemString {
    public final String itemId;
    public final double damage;
    public final double armor;
    public final String variant;

    public ItemString(String itemId, double damage, double armor, String variant) {
        this.itemId = itemId;
        this.damage = damage;
        this.armor = armor;
        this.variant = variant;
    }

    public static ItemString parse(String string) {
        String[] data = string.split(";");
        String id = null;
        String model = null;
        double damage = 0;
        double armor = 0;
        id = data[0];
        if (data.length > 1) {
            for (int i = 1; i < data.length; i++) {
                String k = data[i].toLowerCase();
                if (k.startsWith("damage=")) {
                    damage = Double.parseDouble(k.substring(7));
                } else if (k.startsWith("model=")) {
                    model = k.substring(6);
                } else if (k.startsWith("armor=")) {
                    armor = Double.parseDouble(k.substring(6));
                } else {
                    Log.warn("Could not parse item " + string);
                }
            }
        }
        return new ItemString(id.toLowerCase(), damage, armor, model);
    }

    public static class InvalidItemStringException extends RuntimeException {

        private InvalidItemStringException(String s) {
            super(s);
        }
    }
}
