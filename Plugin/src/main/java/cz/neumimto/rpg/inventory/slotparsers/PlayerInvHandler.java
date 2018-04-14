package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Map;
import java.util.Optional;

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

    public abstract void initHandler();

    /**
     * Method called when
     *  - Player connects
     *  - Player changes world
     *  - Player gain level
     *  - Player learn/upgrade skill
     *  - Player changes weapon
     *  - Player changes hotbar slot
     *  - Player changes armor/accessories
     *
     *  Item init order is in a way player equiped items
     *  @see cz.neumimto.rpg.players.CharacterBase#inventoryEquipSlotOrder
     *  As last
     *
     * @param character
     */
    public abstract void initializeCharacterInventory(IActiveCharacter character);


    protected boolean checkForSlot(IActiveCharacter character, Slot slot) {
        Optional<ItemStack> peek = slot.peek();
        if (peek.isPresent())
            return checkForItem(character, peek.get());
        return false;
    }

    protected boolean checkForItem(IActiveCharacter character, ItemStack itemStack) {
        CannotUseItemReson cannotUseItemReson = inventoryService().canUse(itemStack, character);
        return cannotUseItemReson == CannotUseItemReson.OK;
    }

    protected void initializeItemStack(IActiveCharacter character, Slot query) {
        Map<IGlobalEffect, EffectParams> itemEffects = inventoryService().getItemEffects(query.peek().get());
        effectService().applyGlobalEffectsAsEnchantments(itemEffects, character, null); //todo
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
