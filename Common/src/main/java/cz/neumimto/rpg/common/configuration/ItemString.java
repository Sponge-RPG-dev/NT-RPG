package cz.neumimto.rpg.common.configuration;

import cz.neumimto.rpg.common.logging.Log;

public final class ItemString {
    public final String itemId;
    public final double damage;
    public final double armor;
    public final String variant;
    public final String permission;

    public ItemString(String itemId, double damage, double armor, String variant, String permission) {
        this.itemId = itemId;
        this.damage = damage;
        this.armor = armor;
        this.variant = variant;
        this.permission = permission;
    }

    public static ItemString parse(String string) {
        String[] data = string.split(";");
        String id = null;
        String model = null;
        double damage = 0;
        double armor = 0;
        String permission = null;
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
                } else if (k.startsWith("permission=")) {
                    permission = k.substring(11);
                } else {
                    Log.warn("Could not parse item " + string);
                }
            }
        }
        return new ItemString(id.toLowerCase(), damage, armor, model, permission);
    }

    public int modelOrZero() {
        if (variant == null)
            return 0;
        return Integer.parseInt(variant);
    }

    public static class InvalidItemStringException extends RuntimeException {

        private InvalidItemStringException(String s) {
            super(s);
        }
    }
}
