package cz.neumimto.rpg.sponge;

import com.google.inject.*;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IPropertyService;
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
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.sponge.assets.SpongeAssetService;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.effects.SpongeEffectService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.configuration.SpongeMobSettingsDao;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.events.SpongeEventFactory;
import cz.neumimto.rpg.sponge.exp.SpongeExperienceService;
import cz.neumimto.rpg.sponge.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.sponge.permission.SpongePermissionService;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import cz.neumimto.rpg.sponge.utils.Placeholders;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.CauseStackManager;

public class SpongeGuiceModule extends AbstractModule {

    private final NtRpgPlugin ntRpgPlugin;
    private final Logger logger;
    private final Game game;
    private CauseStackManager causeStackManager;

    public SpongeGuiceModule(NtRpgPlugin ntRpgPlugin, Logger logger, Game game, CauseStackManager causeStackManager) {

        this.ntRpgPlugin = ntRpgPlugin;
        this.logger = logger;
        this.game = game;
        this.causeStackManager = causeStackManager;
    }

    @Override
    protected void configure() {
        bind(SkillTreeDao.class).to(SkillTreeLoaderImpl.class);
        bind(SpongeSkillService.class);
        bind(IPropertyService.class).to(PropertyService.class);
        bind(PartyService.class).to(SpongePartyService.class);

        bind(IScriptEngine.class).to(JSLoader.class);
        bind(CharacterClassDao.class);
        bind(ClassDefinitionDao.class);
        bind(PlayerDao.class);
        bind(IPlayerMessage.class).to(VanillaMessaging.class);
        bind(ClassGenerator.class).to(SpongeClassGenerator.class);
        bind(ClassService.class).to(ClassServiceImpl.class);
        bind(ResourceLoader.class);
        bind(CommandService.class);
        bind(DamageService.class).to(SpongeDamageService.class);
        bind(IEffectService.class).to(SpongeEffectService.class);
        bind(EntityService.class).to(SpongeEntityService.class);
        bind(MobSettingsDao.class).to(SpongeMobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(SpongeExperienceService.class);
        bind(ItemLoreBuilderService.class);
        bind(ParticleDecorator.class);
        bind(VanillaMessaging.class);
        bind(ItemService.class).to(SpongeItemService.class);
        bind(CharacterInventoryInteractionHandler.class).to(InventoryHandler.class);
        bind(InventoryService.class).to(SpongeInventoryService.class);
        bind(RWDao.class);
        bind(RWService.class);


        if (NtRpgPlugin.INTEGRATIONS.contains("Placeholders")) {
            bind(Placeholders.class);
        }

        bind(PermissionService.class).to(SpongePermissionService.class);
        bind(EventFactoryService.class).to(SpongeEventFactory.class);
        bind(LocalizationService.class).to(LocalizationServiceImpl.class);
        bind(SkillService.class).to(SpongeSkillService.class);
        bind(AssetService.class).to(SpongeAssetService.class);

        bind(new TypeLiteral<ICharacterService>() {
        }).toProvider(SpongeCharacterServiceProvider.class);
        bind(new TypeLiteral<ICharacterService<? extends IActiveCharacter>>() {
        }).to(SpongeCharacterService.class);
        bind(new TypeLiteral<ICharacterService<? super IActiveCharacter>>() {
        })
                .toProvider(SpongeCharacterServiceProvider1.class);//.toProvider(() -> (ICharacterService) spongeCharacterServise);
        //   bind(new TypeLiteral<ICharacterService<IActiveCharacter>>(){})
        //           .toProvider(SpongeCharacterServiceProvider.class);
        bind(Game.class).toProvider(() -> game);
        bind(NtRpgPlugin.class).toProvider(() -> ntRpgPlugin);
        bind(Logger.class).toProvider(() -> logger);
        bind(CauseStackManager.class).toProvider(() -> causeStackManager);
    }


    private static SpongeCharacterService scs;

    public static class SpongeCharacterServiceProvider implements Provider<ICharacterService<IActiveCharacter>> {

        @Inject
        private Injector injector;


        @Override
        public ICharacterService get() {
            if (scs == null) {
                scs = injector.getInstance(SpongeCharacterService.class);
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
                scs = injector.getInstance(SpongeCharacterService.class);
            }
            return (ICharacterService) scs;
        }
    }
}
