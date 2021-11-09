package cz.neumimto.rpg.common.inventory;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.common.items.subtypes.ItemSubtypes;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.utils.Console;
import cz.neumimto.rpg.common.assets.AssetService;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.common.logging.Log.error;


public abstract class AbstractInventoryService<T extends IActiveCharacter> implements InventoryService<T> {

    @Inject
    private PermissionService permissionService;

    @Inject
    private AssetService assetService;

    @Inject
    private ItemService itemService;

    protected Map<Class<?>, ManagedInventory> managedInventories = new HashMap<>();

    //todo put to configuration
    private Set<Integer> armorIds = new HashSet<Integer>() {{
        add(36);
        add(37);
        add(38);
        add(39);
    }};

    private Integer offhandId = 40;


    @Override
    public void initializeManagedSlots(T activeCharacter) {
        Map<Class<?>, RpgInventory> managedInventory = activeCharacter.getManagedInventory();
        for (Map.Entry<Class<?>, ManagedInventory> entry : managedInventories.entrySet()) {
            Class<?> key = entry.getKey();
            ManagedInventory mi = entry.getValue();
            RpgInventoryImpl rpgInventory = new RpgInventoryImpl();
            for (SlotEffectSource value : mi.getSlots().values()) {
                ManagedSlot slot;
                if (value.getSlotId() == offhandId) {
                    slot = new FilteredManagedSlotImpl(value.getSlotId(),
                            item -> itemService.checkItemPermission(activeCharacter, item) && activeCharacter.canUse(item.getItemType(), EntityHand.OFF));
                } else if (armorIds.contains(value.getSlotId())) {
                    slot = new FilteredManagedSlotImpl(value.getSlotId(),
                            item -> itemService.checkItemPermission(activeCharacter, item) && activeCharacter.canWear(item.getItemType()));
                } else if (value.getSlotId() >= 0 && value.getSlotId() < 9) {
                    slot = new FilteredManagedSlotImpl(value.getSlotId(), item
                            -> itemService.checkItemPermission(activeCharacter, item) && activeCharacter.canUse(item.getItemType(), EntityHand.MAIN));
                    //                           || item.getItemType().getItemClass() == ItemClass.ARMOR
                    //                         || item.getItemType().getItemClass() == ItemClass.SHIELD);
                } else {
                    slot = new ManagedSlotImpl(value.getSlotId());
                }
                rpgInventory.getManagedSlots().put(value.getSlotId(), slot);
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

        try (FileConfig c = FileConfig.of(path)) {
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
