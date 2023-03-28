package cz.neumimto.rpg.common.inventory;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.permissions.PermissionService;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
            for (Integer value : mi.getSlots().keySet()) {
                ManagedSlot slot = null;
                if (value.equals(offhandId)) {
                    slot = new FilteredManagedSlotImpl(value,
                            item -> itemService.checkItemPermission(activeCharacter, item, EntityHand.OFF.name()));
                } else if (armorIds.contains(value)) {
                    slot = new FilteredManagedSlotImpl(value,
                            item -> itemService.checkItemPermission(activeCharacter, item, "armor"));
                } else if (value >= 0 && value < 9) {
                    slot = new FilteredManagedSlotImpl(value, item
                            -> itemService.checkItemPermission(activeCharacter, item, EntityHand.MAIN.name()));
                }
                rpgInventory.getManagedSlots().put(value, slot);
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
    }

}
