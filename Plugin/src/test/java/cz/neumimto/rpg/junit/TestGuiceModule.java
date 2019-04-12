package cz.neumimto.rpg.junit;

import com.google.inject.AbstractModule;
import cz.neumimto.core.localization.LocalizationService;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.commands.CommandService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.impl.TestItemService;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.TestEffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.MobSettingsDao;
import cz.neumimto.rpg.exp.ExperienceDAO;
import cz.neumimto.rpg.exp.ExperienceService;
import cz.neumimto.rpg.gui.GuiService;
import cz.neumimto.rpg.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.gui.VanillaMessaging;
import cz.neumimto.rpg.inventory.SpongeInventoryService;
import cz.neumimto.rpg.inventory.runewords.RWDao;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.persistance.*;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.SpongeCharacterService;
import cz.neumimto.rpg.players.parties.PartyService;
import cz.neumimto.rpg.properties.SpongePropertyService;
import cz.neumimto.rpg.skills.SkillService;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

public class TestGuiceModule extends AbstractModule {

        @Override
        protected void configure() {
        bind(SkillService.class);
        bind(SpongePropertyService.class);
        bind(PartyService.class);
        bind(CharacterService.class).to(SpongeCharacterService.class);


        bind(CharacterClassDao.class);
        bind(ClassDefinitionDao.class);
        bind(DirectAccessDao.class);
        bind(PlayerDao.class);
        bind(SkillTreeDao.class);

        bind(ClassGenerator.class);
        bind(ClassService.class);
        bind(GlobalScope.class);
        bind(ResourceLoader.class);
        bind(CommandService.class);
        bind(DamageService.class);
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
        bind(ItemService.class).to(TestItemService.class);
        bind(RWDao.class);
        bind(RWService.class);

        bind(Logger.class).toInstance(LoggerFactory.getLogger("TestLogger"));
        bind(PluginContainer.class).toInstance(Mockito.mock(PluginContainer.class));
        bind(NtRpgPlugin.class).toProvider(NtRpgPlugin::new);
        bind(Game.class).toInstance(Mockito.mock(Game.class));
        bind(LocalizationService.class).toInstance(new LocalizationService());
    }
}

