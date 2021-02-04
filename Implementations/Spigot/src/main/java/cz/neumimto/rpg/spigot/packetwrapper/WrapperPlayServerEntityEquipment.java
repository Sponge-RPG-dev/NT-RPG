/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.neumimto.rpg.spigot.packetwrapper;

import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;

    /**
     * All {@link ItemSlot} enum's values stored for faster access
     */
    protected static final ItemSlot[] ITEM_SLOTS = ItemSlot.values();

    protected static int MINOR_VERSION = 16;
    protected static final boolean SUPPORTS_MULTIPLE_SLOTS = MINOR_VERSION >= 16;

    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityEquipment(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Checks if this packet supports multiple slots.
     *
     * @return {@code true} if this packet supports multiple slots and {@code false} otherwise
     */
    public static boolean supportMultipleSlots() {
        return SUPPORTS_MULTIPLE_SLOTS;
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public List<Pair<ItemSlot, ItemStack>> getContents() {
        if (MINOR_VERSION >= 16) return handle.getSlotStackPairLists().read(0);
        throw new UnsupportedOperationException("Unsupported on versions less than 1.16");
    }

    public void setContents(List<Pair<ItemSlot, ItemStack>> contents) {
        if (MINOR_VERSION >= 16) handle.getSlotStackPairLists().write(0, contents);
        else throw new UnsupportedOperationException("Unsupported on versions less than 1.16");
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public ItemSlot getSlot() {
        if (MINOR_VERSION >= 16) {
            final List<Pair<ItemSlot, ItemStack>> slots = handle.getSlotStackPairLists().read(0);
            switch (slots.size()) {
                case 0: return null;
                case 1: return slots.get(0).getFirst();
                default: throw new UnsupportedOperationException("This packet has multiple slots specified");
            }
        }

        if (MINOR_VERSION >= 9) return handle.getItemSlots().read(0);
        int slot = handle.getIntegers().read(0);
        if (slot >= ITEM_SLOTS.length) throw new IllegalArgumentException("Unknown item slot received: " + slot);
        return ITEM_SLOTS[slot];
    }

    public void setSlot(ItemSlot value) {
        if (MINOR_VERSION >= 16) {
            final StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = handle.getSlotStackPairLists();
            List<Pair<ItemSlot, ItemStack>> slots = modifier.read(0);
            switch (slots.size()) {
                case 0: {
                    slots.add(new Pair<>(value, null));
                    modifier.write(0, slots);
                    return;
                }
                case 1: {
                    slots.get(0).setFirst(value);
                    modifier.write(0, slots);
                    return;
                }
                default: throw new UnsupportedOperationException("This packet has multiple slots specified");
            }
        }

        if (MINOR_VERSION >= 9) handle.getItemSlots().write(0, value);
        else switch (value) {
            case MAINHAND: {
                handle.getIntegers().write(1, 0);
                break;
            }
            case FEET: {
                handle.getIntegers().write(1, 1);
                break;
            }
            case LEGS: {
                handle.getIntegers().write(1, 2);
                break;
            }
            case CHEST: {
                handle.getIntegers().write(1, 3);
                break;
            }
            case HEAD: {
                handle.getIntegers().write(1, 4);
                break;
            }
            case OFFHAND: throw new IllegalArgumentException("Offhand is not available on 1.8 or less");
        }
    }

    /**
     * Retrieve Item.
     * <p>
     * Notes: item in slot format
     *
     * @return The current Item
     */
    public ItemStack getItem() {
        if (MINOR_VERSION >= 16) {
            final List<Pair<ItemSlot, ItemStack>> slots = handle.getSlotStackPairLists().read(0);
            switch (slots.size()) {
                case 0: return null;
                case 1: return slots.get(0).getSecond();
                default: throw new UnsupportedOperationException("This packet has multiple slots specified");
            }
        }

        return handle.getItemModifier().read(0);
    }

    /**
     * Set Item.
     *
     * @param value - new value.
     */
    public void setItem(ItemStack value) {
        if (MINOR_VERSION >= 16) {
            final StructureModifier<List<Pair<ItemSlot, ItemStack>>> modifier = handle.getSlotStackPairLists();
            List<Pair<ItemSlot, ItemStack>> slots = modifier.read(0);
            switch (slots.size()) {
                case 0: {
                    slots.add(new Pair<>(null, value));
                    modifier.write(0, slots);
                    return;
                }
                case 1: {
                    slots.get(0).setSecond(value);
                    modifier.write(0, slots);
                    return;
                }
                default: throw new UnsupportedOperationException("This packet has multiple slots specified");
            }
        }

        handle.getItemModifier().write(0, value);
    }
}