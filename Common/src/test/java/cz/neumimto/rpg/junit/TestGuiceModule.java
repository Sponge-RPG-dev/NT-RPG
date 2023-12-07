package cz.neumimto.rpg.junit;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import cz.neumimto.rpg.TestApiImpl;
import cz.neumimto.rpg.TestDamageService;
import cz.neumimto.rpg.TestSkillService;
import cz.neumimto.rpg.assets.TestAssetService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.TestPartyService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.TestMobSettingsDao;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.events.TestEventFactory;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.exp.ExperienceService;
import cz.neumimto.rpg.common.experience.TestExperienceService;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.inventory.TestInventoryService;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.skills.reagents.Cooldown;
import cz.neumimto.rpg.common.skills.reagents.HPCast;
import cz.neumimto.rpg.common.skills.reagents.ManaCast;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.entity.TestEntityService;
import cz.neumimto.rpg.entity.TestResourceService;
import cz.neumimto.rpg.model.TestPersistanceHandler;
import cz.neumimto.rpg.persistence.InMemoryPlayerStorage;
import cz.neumimto.rpg.skills.reagents.CooldownTest;
import cz.neumimto.rpg.sponge.permission.TestPermissionService;

import java.lang.reflect.Proxy;

public class TestGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EffectService.class).to(TestEffectService.class);
        bind(SkillService.class).to(TestSkillService.class);
        bind(PropertyService.class).to(TestPropertyService.class);
        bind(PartyService.class).to(TestPartyService.class);
        bind(IPersistenceHandler.class).to(TestPersistanceHandler.class);
        bind(ICharacterClassDao.class).toProvider(() -> c -> {
        });
        bind(ClassDefinitionDao.class);
        bind(IPlayerDao.class).to(InMemoryPlayerStorage.class);
        bind(ExperienceService.class).to(TestExperienceService.class);
        bind(ClassService.class);
        bind(ResourceLoader.class);
        bind(DamageService.class).to(TestDamageService.class);
        bind(EffectService.class).to(TestEffectService.class);
        bind(EntityService.class).to(TestEntityService.class);
        bind(MobSettingsDao.class).to(TestMobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(ResourceService.class).to(TestResourceService.class);

        bind(IPlayerMessage.class).toProvider(() -> {
            try {
                IPlayerMessage o = (IPlayerMessage) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{IPlayerMessage.class},
                        (proxy, method, args) -> {
                            Log.info(method.getName());
                            return null;
                        });
                return o;
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException(":(");
        });
        bind(InventoryService.class).to(TestInventoryService.class);
        bind(ItemService.class).to(TestItemService.class);
        bind(RpgApi.class).to(TestApiImpl.class);

        bind(PermissionService.class).to(TestPermissionService.class);
        bind(EventFactoryService.class).to(TestEventFactory.class);
        bind(LocalizationService.class).to(LocalizationServiceImpl.class);
        bind(SkillService.class).to(TestSkillService.class);
        bind(AssetService.class).to(TestAssetService.class);
        bind(CharacterService.class).to(TestCharacterService.class);

        //bind(new TypeLiteral<CharacterService<ActiveCharacter>>() {
        //})
        //        .toProvider(SpongeCharacterServiceProvider1.class);//.toProvider(() -> (CharacterService) TestCharacterService);

        bind(Cooldown.class).to(CooldownTest.class);
        bind(HPCast.class);
        bind(ManaCast.class);

    }

    private static TestCharacterService scs;

    public static class TestCharacterServiceProvider implements Provider<TestCharacterService> {

        @Inject
        private Injector injector;

        @Override
        public TestCharacterService get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return scs;
        }
    }

    public static class SpongeCharacterServiceProvider1 implements Provider<CharacterService<ActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public CharacterService<ActiveCharacter> get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return null;
        }
    }
}

