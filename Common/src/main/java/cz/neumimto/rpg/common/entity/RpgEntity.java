package cz.neumimto.rpg.common.entity;

public interface RpgEntity {

    boolean isPlayer();

    default boolean isMonster() {
        return !isPlayer();
    }
}
