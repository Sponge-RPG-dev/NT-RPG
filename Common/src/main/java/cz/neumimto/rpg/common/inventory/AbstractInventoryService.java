package cz.neumimto.rpg.common.inventory;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.inventory.RpgInventory;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.utils.Console;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.api.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.api.items.subtypes.ItemSubtypes;

import javax.inject.Inject;
import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.error;


public abstract class AbstractInventoryService<T extends IActiveCharacter> implements InventoryService<T> {

    @Inject
    private AssetService assetService;

    @Inject
    private ItemService itemService;

    protected Map<Class<?>, ManagedInventory> managedInventories = new HashMap<>();

    @Override
    public void initializeManagedSlots(T activeCharacter) {
        Map<Class<?>, RpgInventory> managedInventory = activeCharacter.getManagedInventory();
        for (Map.Entry<Class<?>, ManagedInventory> entry : managedInventories.entrySet()) {
            Class<?> key = entry.getKey();
            ManagedInventory mi = entry.getValue();
            RpgInventoryImpl rpgInventory = new RpgInventoryImpl();
            for (SlotEffectSource value : mi.getSlots().values()) {
                rpgInventory.getManagedSlots().put(value.getSlotId(), new ManagedSlotImpl(value.getSlotId()));
            }
            managedInventory.put(key, rpgInventory);
        }
    }

    @Override
    public boolean isManagedInventory(Class aClass, int slotId) {
        ManagedInventory managedInventory = managedInventories.get(aClass);
        return managedInventory != null && managedInventory.getSlots().containsKey(slotId);
    }

    @Override
    public void reload() {
        managedInventories.clear();
        load();
    }

    @Override
    public void load() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "ItemGroups.conf");
        File f = path.toFile();
        if (!f.exists()) {
            assetService.copyToFile("ItemGroups.conf", path);
        }

        try (FileConfig c = FileConfig.of(path)){
            c.load();

            List<? extends Config> inventorySlots = c.get("InventorySlots");
            for (Config inventorySlot : inventorySlots) {
                loadInventorySettings(inventorySlot);
            }

            List<String> itemMetaSubtypes = c.get("ItemMetaSubtypes");

            itemMetaSubtypes.stream().map(ItemSubtype::new).forEach(a -> itemService.getItemSubtypes().get(a));

        }
    }

    private void loadInventorySettings(Config slots) {
        String aClass = slots.get("type");
        try {
            Class<?> aClass1 = Class.forName(aClass);

            HashMap<Integer, SlotEffectSource> slotEffectSourceHashMap = new HashMap<>();
            ManagedInventory managedInventory = new ManagedInventory(aClass1, slotEffectSourceHashMap);
            List<Object> cslots = slots.get("slots");
            List<String> stringList = cslots.stream().map(Object::toString).collect(Collectors.toList());
            for (String str : stringList) {
                String[] split = str.split(";");
                if (split.length == 1) {
                    SlotEffectSource slotEffectSource = new SlotEffectSource(Integer.parseInt(split[0]), ItemSubtypes.ANY);
                    slotEffectSourceHashMap.put(slotEffectSource.getSlotId(), slotEffectSource);
                } else {
                    ItemSubtype type = itemService.getItemSubtypes().get(split[1]);
                    if (type == null) {
                        type = ItemSubtypes.ANY;
                        error("Could not find subtype " + split[1]);
                    }
                    SlotEffectSource slotEffectSource = new SlotEffectSource(Integer.parseInt(split[0]), type);
                    slotEffectSourceHashMap.put(slotEffectSource.getSlotId(), slotEffectSource);
                }
            }
            managedInventories.put(managedInventory.getType(), managedInventory);
        } catch (ClassNotFoundException e) {
            error(Console.RED + "Could not find inventory type " + Console.GREEN + aClass + Console.RED
                    + " defined in ItemGroups.conf. Is the mod loaded? Is the class name correct? If you are unsure restart plugin with debug mode "
                    + "ON and interact with desired inventory");
        }
    }

}
