package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.AbstractRpgGuiceModule;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.exp.ExperienceService;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.nms.NMSHandler;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.assets.SpigotAssetService;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.SpigotEffectService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.configuration.SpigotMobSettingsDao;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotPartyService;
import cz.neumimto.rpg.spigot.events.SpigotEventFactory;
import cz.neumimto.rpg.spigot.exp.SpigotExperienceService;
import cz.neumimto.rpg.spigot.gui.SpigotGui;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import cz.neumimto.rpg.spigot.permissions.SpigotPermissionService;
import cz.neumimto.rpg.spigot.scripting.mechanics.SpigotEntityUtils;
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;

import java.util.Map;
import java.util.ServiceLoader;

public class SpigotGuiceModule extends AbstractRpgGuiceModule {

    private final SpigotRpgPlugin ntRpgPlugin;
    private SpigotRpg spigotRpg;
    private Map extraBindings;
    private Map<Class, Object> providers;
    private String minecraftVersion;

    public SpigotGuiceModule(SpigotRpgPlugin ntRpgPlugin, SpigotRpg spigotRpg, Map extraBindings, Map providers, String minecraftVersion) {
        this.ntRpgPlugin = ntRpgPlugin;
        this.spigotRpg = spigotRpg;
        this.extraBindings = extraBindings;
        this.providers = providers;
        this.minecraftVersion = minecraftVersion;
    }

    @Override
    protected Map getBindings() {
        Map map = super.getBindings();
        map.put(SkillService.class, SpigotSkillService.class);
        map.put(PartyService.class, SpigotPartyService.class);
        map.put(IPlayerMessage.class, SpigotGui.class);
        map.put(ClassGenerator.class, SpigotClassGenerator.class);
        map.put(DamageService.class, SpigotDamageService.class);
        map.put(EffectService.class, SpigotEffectService.class);
        map.put(EntityService.class, SpigotEntityService.class);
        map.put(MobSettingsDao.class, SpigotMobSettingsDao.class);
        map.put(ExperienceService.class, SpigotExperienceService.class);
        map.put(ItemService.class, SpigotItemService.class);
        map.put(InventoryService.class, SpigotInventoryService.class);
        map.put(AssetService.class, SpigotAssetService.class);
        map.put(PermissionService.class, SpigotPermissionService.class);
        map.put(EventFactoryService.class, SpigotEventFactory.class);
        map.put(CharacterInventoryInteractionHandler.class, InventoryHandler.class);
        map.put(ResourceLoader.class, SpigotResourceManager.class);
        map.put(RWDao.class, null);
        map.put(CharacterService.class, SpigotCharacterService.class);

        ServiceLoader<NMSHandler> load = ServiceLoader.load(NMSHandler.class, this.getClass().getClassLoader());
        for (NMSHandler nmsHandler : load) {
            if (nmsHandler.getVersion().contains(minecraftVersion)) {
                map.put(NMSHandler.class, nmsHandler.getClass());
            }
        }
        if (!map.containsKey(NMSHandler.class)) {
            Log.error(" !! NTRPG is not compatible with this version of mc, some features wont work");
            map.put(NMSHandler.class, NMSHandler.class);
        }


        map.put(SpigotEntityUtils.class, null);
        //map.put(ICharacterClassDao.class).to(JPACharacterClassDao.class);
        //map.put(IPlayerDao.class).to(JPAPlayerDao.class);
        map.putAll(extraBindings);
        return map;
    }

    @Override
    protected void configure() {
        super.configure();

        bind(SpigotRpgPlugin.class).toProvider(() -> ntRpgPlugin);
        bind(SpigotRpg.class).toProvider(() -> spigotRpg);
        for (Map.Entry<Class, Object> entry : providers.entrySet()) {
            bind(entry.getKey()).toProvider(() -> entry.getValue());
        }
    }

}
