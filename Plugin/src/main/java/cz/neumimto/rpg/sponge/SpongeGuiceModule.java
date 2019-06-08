package cz.neumimto.rpg.sponge;

import com.google.inject.AbstractModule;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.effect.EventFactoryService;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.inventory.crafting.runewords.RWDao;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.DirectAccessDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.sponge.assets.SpongeAssetService;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.events.SpongeEventFactory;
import cz.neumimto.rpg.sponge.exp.ExperienceService;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.sponge.permission.SpongePermissionService;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.effects.SpongeEffectService;
import cz.neumimto.rpg.sponge.gui.GuiService;
import cz.neumimto.rpg.sponge.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import cz.neumimto.rpg.sponge.utils.Placeholders;

public class SpongeGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SpongeSkillService.class);
        bind(PropertyService.class).to(SpongePropertyService.class);
        bind(PartyService.class).to(SpongePartyService.class);
        bind(CharacterService.class).to(SpongeCharacterServise.class);


        bind(CharacterClassDao.class);
        bind(ClassDefinitionDao.class);
        bind(DirectAccessDao.class);
        bind(PlayerDao.class);
        bind(SkillTreeDao.class);

        bind(ClassGenerator.class).to(SpongeClassGenerator.class);
        bind(ClassService.class).to(ClassServiceImpl.class);
        bind(GlobalScope.class);
        bind(ResourceLoader.class);
        bind(CommandService.class);
        bind(DamageService.class).to(SpongeDamageService.class);
        bind(EffectService.class).to(SpongeEffectService.class);
        bind(EntityService.class).to(SpongeEntityService.class);
        bind(MobSettingsDao.class);
        bind(ExperienceDAO.class);
        bind(ExperienceService.class);
        bind(GuiService.class);
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
        bind(ISkillService.class).to(SpongeSkillService.class);
        bind(AssetService.class).to(SpongeAssetService.class);
    }
}
