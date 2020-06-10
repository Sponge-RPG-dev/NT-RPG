package cz.neumimto.rpg.junit;

import com.google.inject.*;
import cz.neumimto.rpg.TestApiImpl;
import cz.neumimto.rpg.TestDamageService;
import cz.neumimto.rpg.TestResourceLoader;
import cz.neumimto.rpg.TestSkillService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.assets.TestAssetService;
import cz.neumimto.rpg.common.TestPartyService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.common.effects.AbstractEffectService;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.entity.configuration.TestMobSettingsDao;
import cz.neumimto.rpg.common.events.TestEventFactory;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.experience.TestExperienceService;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.TestInventoryService;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.common.skills.reagents.Cooldown;
import cz.neumimto.rpg.common.skills.reagents.HPCast;
import cz.neumimto.rpg.common.skills.reagents.ManaCast;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.entity.TestEntityService;
import cz.neumimto.rpg.model.TestPersistanceHandler;
import cz.neumimto.rpg.persistence.InMemoryPlayerStorage;
import cz.neumimto.rpg.skills.reagents.CooldownTest;
import cz.neumimto.rpg.sponge.permission.TestPermissionService;
import jdk.nashorn.api.scripting.JSObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;


public class TestGuiceModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(SkillTreeDao.class).to(SkillTreeLoaderImpl.class);
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
        bind(ClassGenerator.class).toProvider(() -> new ClassGenerator() {
            @Override
            public void generateDynamicListener(List<JSObject> list) {}

            @Override
            protected Type getListenerSubclass() {
                return null;
            }

            @Override
            protected DynamicType.Builder<Object> visitImplSpecAnnListener(DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Object> classBuilder, JSObject object) {
                return null;
            }
        });
        bind(ClassService.class).to(ClassServiceImpl.class);
        bind(ResourceLoader.class).to(TestResourceLoader.class);
        bind(DamageService.class).to(TestDamageService.class);
        bind(AbstractEffectService.class).to(TestEffectService.class);
        bind(EntityService.class).to(TestEntityService.class);
        bind(MobSettingsDao.class).to(TestMobSettingsDao.class);
        bind(ExperienceDAO.class);

        Class<IPlayerMessage> type = (Class<IPlayerMessage>) new ByteBuddy()
                .subclass(Object.class)
                .implement(IPlayerMessage.class)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new MethodInterceptor()))
                .make().load(IPlayerMessage.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        bind(IPlayerMessage.class).toProvider(() -> {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException(":(");
        });
        bind(InventoryService.class).to(TestInventoryService.class);
        bind(CharacterInventoryInteractionHandler.class).to(InventoryHandler.class);
        bind(ItemService.class).to(TestItemService.class);
        bind(RWDao.class);
        bind(RpgApi.class).to(TestApiImpl.class);

        bind(IScriptEngine.class).to(JSLoader.class);

        bind(PermissionService.class).to(TestPermissionService.class);
        bind(EventFactoryService.class).to(TestEventFactory.class);
        bind(LocalizationService.class).to(LocalizationServiceImpl.class);
        bind(SkillService.class).to(TestSkillService.class);
        bind(AssetService.class).to(TestAssetService.class);
        bind(new TypeLiteral<CharacterService<? extends IActiveCharacter>>() {
        }).to(TestCharacterService.class);

        bind(CharacterService.class).toProvider(SpongeCharacterServiceProvider.class);

        bind(new TypeLiteral<CharacterService<? super IActiveCharacter>>() {
        })
                .toProvider(SpongeCharacterServiceProvider1.class);//.toProvider(() -> (CharacterService) TestCharacterService);
        bind(new TypeLiteral<CharacterService<IActiveCharacter>>() {
        })
                .toProvider(SpongeCharacterServiceProvider.class);
        bind(Cooldown.class).to(CooldownTest.class);
        bind(HPCast.class);
        bind(ManaCast.class);
    }

    private static TestCharacterService scs;

    public static class SpongeCharacterServiceProvider implements Provider<CharacterService<IActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public CharacterService get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return scs;
        }
    }

    public static class SpongeCharacterServiceProvider1 implements Provider<CharacterService<? super IActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public CharacterService<? super IActiveCharacter> get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return (CharacterService) scs;
        }
    }


    public class MethodInterceptor implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.info(method.getName());
            return null;
        }
    }
}

