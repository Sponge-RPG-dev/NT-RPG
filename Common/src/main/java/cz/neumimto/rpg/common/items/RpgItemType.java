package cz.neumimto.rpg.common.items;

import java.util.function.BiFunction;

public interface RpgItemType {

    BiFunction<String, String, String> KEY_BUILDER = (s, s2) -> s2 == null ? s : s.concat(";").concat(s2);

    ItemClass getItemClass();

    String getId();

    String getModelId();

    default String getKey() {
        return KEY_BUILDER.apply(getId(), getModelId());
    }

    String getPermission();
}
