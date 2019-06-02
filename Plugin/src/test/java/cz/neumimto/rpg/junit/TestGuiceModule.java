package cz.neumimto.rpg.junit;

import com.google.inject.AbstractModule;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.events.effect.EventFactoryService;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.assets.TestAssetService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.configuration.SkillTreeDao;
import cz.neumimto.rpg.common.events.TestEventFactory;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.DirectAccessDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.permission.TestPermissionService;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.TestPropertyService;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.MobSettingsDao;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.sponge.exp.ExperienceService;
import cz.neumimto.rpg.sponge.gui.GuiService;
import cz.neumimto.rpg.sponge.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWDao;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.players.parties.PartyService;
import cz.neumimto.rpg.common.scripting.JSLoader;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

public class TestGuiceModule extends AbstractModule {

        @Override
        protected void configure() {
        bind(SpongeSkillService.class);
        bind(PropertyService.class).to(TestPropertyService.class);
        bind(PartyService.class);
        bind(CharacterService.class).to(TestCharacterService.class);


        bind(CharacterClassDao.class);
        bind(ClassDefinitionDao.class);
        bind(DirectAccessDao.class);
        bind(PlayerDao.class);
        bind(SkillTreeDao.class);

        bind(ClassGenerator.class).to(SpongeClassGenerator.class);
        bind(ClassService.class);
        bind(GlobalScope.class);
        bind(ResourceLoader.class);
        bind(CommandService.class);
        bind(DamageService.class).to(SpongeDamageService.class);
        bind(EffectService.class).to(TestEffectService.class);
        bind(EntityService.class);
        bind(MobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(ExperienceService.class);
        bind(GuiService.class);
        bind(ItemLoreBuilderService.class);
        bind(ParticleDecorator.class);
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

        bind(JSLoader.class);

        bind(PermissionService.class).to(TestPermissionService.class);
        bind(EventFactoryService.class).to(TestEventFactory.class);
        bind(LocalizationService.class).to(LocalizationServiceImpl.class);
        bind(ISkillService.class).to(SpongeSkillService.class);
        bind(AssetService.class).to(TestAssetService.class);
    }
}

