package cz.neumimto.rpg.junit;

import com.google.inject.*;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.TestSkillService;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.assets.TestAssetService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.events.TestEventFactory;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.effects.SpongeEffectService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.configuration.SpongeMobSettingsDao;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.exp.ExperienceService;
import cz.neumimto.rpg.sponge.gui.GuiService;
import cz.neumimto.rpg.sponge.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.permission.TestPermissionService;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import net.bytebuddy.ByteBuddy;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

public class TestGuiceModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(SkillTreeDao.class).to(SkillTreeLoaderImpl.class);
        bind(IEffectService.class).to(SpongeEffectService.class);
        bind(SpongeSkillService.class);
        bind(PropertyService.class).to(TestPropertyService.class);
        bind(PartyService.class).to(SpongePartyService.class);

        bind(CharacterClassDao.class);
        bind(ClassDefinitionDao.class);
        bind(PlayerDao.class);

        bind(ClassGenerator.class).to(SpongeClassGenerator.class);
        bind(ClassService.class).to(ClassServiceImpl.class);
        bind(GlobalScope.class);
        bind(ResourceLoader.class);
        bind(CommandService.class);
        bind(DamageService.class).to(SpongeDamageService.class);
        bind(EffectService.class).to(TestEffectService.class);
        bind(EntityService.class).to(SpongeEntityService.class);
        bind(MobSettingsDao.class).to(SpongeMobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(ExperienceService.class);
        bind(GuiService.class);
        bind(ItemLoreBuilderService.class);
        bind(ParticleDecorator.class);
        Class<? extends IPlayerMessage> type = new ByteBuddy()
                .subclass(IPlayerMessage.class)
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        bind(IPlayerMessage.class).toProvider(() -> {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException(":(");
        });
        bind(VanillaMessaging.class);
        bind(InventoryService.class).to(SpongeInventoryService.class);
        bind(CharacterInventoryInteractionHandler.class).to(InventoryHandler.class);
        bind(ItemService.class).to(TestItemService.class);
        bind(RWDao.class);
        bind(RWService.class);

        bind(Logger.class).toInstance(LoggerFactory.getLogger("TestLogger"));
        bind(PluginContainer.class).toInstance(Mockito.mock(PluginContainer.class));
        bind(NtRpgPlugin.class).toProvider(NtRpgPlugin::new);
        bind(Game.class).toInstance(Mockito.mock(Game.class));

        bind(IScriptEngine.class).to(JSLoader.class);

        bind(PermissionService.class).to(TestPermissionService.class);
        bind(EventFactoryService.class).to(TestEventFactory.class);
        bind(LocalizationService.class).to(LocalizationServiceImpl.class);
        bind(SkillService.class).to(TestSkillService.class);
        bind(AssetService.class).to(TestAssetService.class);
        bind(new TypeLiteral<ICharacterService<? extends IActiveCharacter>>() {
        }).to(TestCharacterService.class);

        bind(ICharacterService.class).toProvider(SpongeCharacterServiceProvider.class);

        bind(new TypeLiteral<ICharacterService<? super IActiveCharacter>>() {
        })
                .toProvider(SpongeCharacterServiceProvider1.class);//.toProvider(() -> (ICharacterService) TestCharacterService);
        bind(new TypeLiteral<ICharacterService<IActiveCharacter>>() {
        })
                .toProvider(SpongeCharacterServiceProvider.class);
    }

    private static TestCharacterService scs;

    public static class SpongeCharacterServiceProvider implements Provider<ICharacterService<IActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public ICharacterService get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return scs;
        }
    }

    public static class SpongeCharacterServiceProvider1 implements Provider<ICharacterService<? super IActiveCharacter>> {

        @Inject
        private Injector injector;

        @Override
        public ICharacterService<? super IActiveCharacter> get() {
            if (scs == null) {
                scs = injector.getInstance(TestCharacterService.class);
            }
            return (ICharacterService) scs;
        }
    }

}

