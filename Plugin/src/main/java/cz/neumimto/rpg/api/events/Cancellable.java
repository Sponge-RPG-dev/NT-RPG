package cz.neumimto.rpg.api.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean state);

}
