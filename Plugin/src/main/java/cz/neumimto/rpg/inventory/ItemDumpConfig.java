package cz.neumimto.rpg.inventory;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.List;
@ConfigSerializable
public class ItemDumpConfig {

    @Setting
    public List<ItemType> chestplate = new ArrayList<>();

    @Setting
    public List<ItemType> boots = new ArrayList<>();

    @Setting
    public List<ItemType> helmet = new ArrayList<>();

    @Setting
    public List<ItemType> leggings = new ArrayList<>();

    @Setting
    public List<ItemType> shield = new ArrayList<>();

    @Setting
    public List<ItemType> sword = new ArrayList<>();

    @Setting
    public List<ItemType> axe = new ArrayList<>();

    @Setting
    public List<ItemType> pickaxe = new ArrayList<>();

    @Setting
    public List<ItemType> shovel = new ArrayList<>();

    @Setting
    public List<ItemType> staff = new ArrayList<>();

    @Setting
    public List<ItemType> hoe = new ArrayList<>();

    @Setting
    public List<ItemType> bow = new ArrayList<>();
}
