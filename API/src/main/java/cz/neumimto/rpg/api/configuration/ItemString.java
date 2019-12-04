package cz.neumimto.rpg.api.configuration;

import cz.neumimto.rpg.api.utils.MathUtils;

public final class ItemString {
    public final String itemId;
    public final double damage;
    public final double armor;
    public final String variant;

    protected ItemString(String itemId, double damage, double armor, String variant) {
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
        switch (data.length) {
            case 1:
                id = data[0];
                break;
            case 2:
                id = data[0];
                if (MathUtils.isNumeric(data[1])) {
                    damage = Double.parseDouble(data[1]);
                } else {
                    model = data[1];
                }
                break;
            case 3:
                id = data[0];
                if (MathUtils.isNumeric(data[1])) {
                    damage = Double.parseDouble(data[1]);
                    model = data[2];
                    break;
                }
                if (MathUtils.isNumeric(data[2])) {
                    damage = Double.parseDouble(data[2]);
                    model = data[1];
                }
                break;
            default:
                throw new InvalidItemStringException("Not possible to resolve argument " + string);
        }
        return new ItemString(id.toLowerCase(), damage, 0, model);
    }

    public static class InvalidItemStringException extends RuntimeException {

        private InvalidItemStringException(String s) {
            super(s);
        }
    }
}
