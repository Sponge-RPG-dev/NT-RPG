package cz.neumimto.rpg.common.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean state);

}
