package cz.neumimto.rpg.api.items;

import java.util.function.BiFunction;

public interface RpgItemType {

    BiFunction<String, String, String> KEY_BUILDER = (s, s2) -> s2 == null ? s : s.concat(";").concat(s2);

    WeaponClass getWeaponClass();

    double getDamage();

    double getArmor();

    String getId();

    String getModelId();

    default String getKey() {
        return KEY_BUILDER.apply(getId(), getModelId());
    }

}
