package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.CatalogType;

/**
 * Created by NeumimTo on 25.3.2018.
 */
public abstract class PlayerInvHandler implements CatalogType {

    private final String name;
    private final String id;

    public PlayerInvHandler(String name) {
        this.id = name.toLowerCase();
        this.name = name;
    }

    public abstract void initializeHotbar(IActiveCharacter character);

    public abstract void initializeArmor(IActiveCharacter character);

    public abstract void changeActiveHotbarSlot(IActiveCharacter character, int slot);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketType that = (SocketType) o;
        return getId().equals(that.getId());
    }
}
