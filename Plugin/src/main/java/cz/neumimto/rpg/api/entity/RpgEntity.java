package cz.neumimto.rpg.api.entity;

public interface RpgEntity {

    boolean isPlayer();

    default boolean isMonster() {
        return !isPlayer();
    }
}
