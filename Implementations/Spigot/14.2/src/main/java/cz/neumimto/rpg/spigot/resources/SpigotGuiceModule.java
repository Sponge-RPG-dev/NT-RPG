package cz.neumimto.rpg.spigot.resources;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.IExperienceService;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.AbstractRpgGuiceModule;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
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
import cz.neumimto.rpg.spigot.skills.SpigotSkillService;

import java.util.Map;

public class SpigotGuiceModule extends AbstractRpgGuiceModule {

    private final SpigotRpgPlugin ntRpgPlugin;
    private Map extraBindings;

    public SpigotGuiceModule(SpigotRpgPlugin ntRpgPlugin, Map extraBindings) {
        this.ntRpgPlugin = ntRpgPlugin;
        this.extraBindings = extraBindings;
    }

    @Override
    protected Map getBindings() {
        Map map = super.getBindings();
        map.put(SkillService.class, SpigotSkillService.class);
        map.put(PartyService.class, SpigotPartyService.class);
        map.put(IPlayerMessage.class, SpigotGui.class);
        map.put(ClassGenerator.class, SpigotClassGenerator.class);
        map.put(DamageService.class, SpigotDamageService.class);
        map.put(IEffectService.class, SpigotEffectService.class);
        map.put(EntityService.class, SpigotEntityService.class);
        map.put(MobSettingsDao.class, SpigotMobSettingsDao.class);
        map.put(IExperienceService.class, SpigotExperienceService.class);
        map.put(ItemService.class, SpigotItemService.class);
        map.put(InventoryService.class, SpigotInventoryService.class);
        map.put(AssetService.class, SpigotAssetService.class);
        map.put(PermissionService.class, SpigotPermissionService.class);
        map.put(EventFactoryService.class, SpigotEventFactory.class);
        map.put(CharacterInventoryInteractionHandler.class, InventoryHandler.class);
        map.put(IResourceLoader.class, SpigotResourceManager.class);

        map.put(RWDao.class, null);
        //map.put(ICharacterClassDao.class).to(JPACharacterClassDao.class);
        //map.put(IPlayerDao.class).to(JPAPlayerDao.class);
        map.putAll(extraBindings);
        return map;
    }

    @Override
    protected void configure() {
        super.configure();

        bind(new TypeLiteral<ICharacterService>() {
        }).toProvider(SpigotCharacterServiceProvider.class);
        bind(new TypeLiteral<ICharacterService<? extends IActiveCharacter>>() {
        }).to(SpigotCharacterService.class);
        bind(new TypeLiteral<ICharacterService<? super IActiveCharacter>>() {
        }).toProvider(SpigotCharacterServiceProvider1.class);

        
        bind(SpigotRpgPlugin.class).toProvider(() -> ntRpgPlugin);
    }
    
    private static SpigotCharacterService scs;

    public static class SpigotCharacterServiceProvider implements Provider<ICharacterService<IActiveCharacter>> {

        @Inject
        private Injector injector;


        @Override
        public ICharacterService get() {
            if (scs == null) {
                scs = injector.getInstance(SpigotCharacterService.class);
            }
            return scs;
        }
    }

    public static class SpigotCharacterServiceProvider1 implements Provider<ICharacterService<? super IActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public ICharacterService<? super IActiveCharacter> get() {
            if (scs == null) {
                scs = injector.getInstance(SpigotCharacterService.class);
            }
            return (ICharacterService) scs;
        }
    }
}
