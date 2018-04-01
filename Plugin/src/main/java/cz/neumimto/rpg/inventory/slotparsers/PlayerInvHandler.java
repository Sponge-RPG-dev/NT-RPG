package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.inventory.InventoryService;
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
        this.id = "nt-rpg:" + name.toLowerCase();
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

    public abstract void onRightClick(IActiveCharacter character, int slot);

    public abstract void onLeftClick(IActiveCharacter character, int slot);


    protected InventoryService inventoryService() {
        return NtRpgPlugin.GlobalScope.inventorySerivce;
    }

    protected EffectService effectService() {
        return NtRpgPlugin.GlobalScope.effectService;
    }
}
