package cz.neumimto.rpg.common.configuration;

import cz.neumimto.rpg.common.logging.Log;

public final class ItemString {
    public static final String permPrefix = "ntrpg.useitem.";

    public final String itemId;
    public final String variant;
    public final String permission;

    public ItemString(String itemId, String variant, String permission) {
        this.itemId = itemId;
        this.variant = variant;
        this.permission = permission;
    }

    public static ItemString parse(String string) {
        String[] data = string.split(";");
        String id = null;
        String model = null;

        String permission = null;
        id = data[0];
        if (data.length > 1) {
            for (int i = 1; i < data.length; i++) {
                String k = data[i].toLowerCase();
                if (k.startsWith("model=")) {
                    model = k.substring(6);
                } else if (k.startsWith("permission=")) {
                    permission = k.substring(11);
                } else {
                    Log.warn("Could not parse item " + string);
                }
            }
        }
        if (permission == null) {
            permission = permPrefix + id.replaceAll(":",".");
        }
        return new ItemString(id.toLowerCase(), model, permission);
    }

    public int modelOrZero() {
        if (variant == null)
            return 0;
        return Integer.parseInt(variant);
    }

}
