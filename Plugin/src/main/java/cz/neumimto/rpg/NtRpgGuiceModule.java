package cz.neumimto.rpg;

import com.google.inject.AbstractModule;
import cz.neumimto.rpg.commands.CommandService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.damage.SpongeEffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.MobSettingsDao;
import cz.neumimto.rpg.exp.ExperienceDAO;
import cz.neumimto.rpg.exp.ExperienceService;
import cz.neumimto.rpg.gui.GuiService;
import cz.neumimto.rpg.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.gui.VanillaMessaging;
import cz.neumimto.rpg.inventory.CustomItemFactory;
import cz.neumimto.rpg.inventory.SpongeInventoryService;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.inventory.runewords.RWDao;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.slotparsers.DefaultPlayerInvHandler;
import cz.neumimto.rpg.persistance.*;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.SpongeCharacterService;
import cz.neumimto.rpg.players.parties.PartyService;
import cz.neumimto.rpg.properties.PropertyService;
import cz.neumimto.rpg.skills.SkillService;

public class NtRpgGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SkillService.class);
        bind(PropertyService.class);
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
        bind(EffectService.class).to(SpongeEffectService.class);
        bind(EntityService.class);
        bind(MobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(ExperienceService.class);
        bind(GuiService.class);
        bind(ItemLoreBuilderService.class);
        bind(ParticleDecorator.class);
        bind(VanillaMessaging.class);
        bind(CustomItemFactory.class);
        bind(SpongeInventoryService.class);
        bind(SpongeItemService.class);
        bind(RWDao.class);
        bind(RWService.class);
        bind(DefaultPlayerInvHandler.class);
    }
}
