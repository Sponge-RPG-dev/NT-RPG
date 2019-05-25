package cz.neumimto.rpg.sponge;

import com.google.inject.AbstractModule;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.inventory.CharacterInventoryInteractionHandler;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.common.configuration.SkillTreeDao;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.inventory.InventoryHandler;
import cz.neumimto.rpg.common.persistance.dao.CharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.persistance.dao.DirectAccessDao;
import cz.neumimto.rpg.common.persistance.dao.PlayerDao;
import cz.neumimto.rpg.damage.SpongeDamageService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.MobSettingsDao;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.sponge.exp.ExperienceService;
import cz.neumimto.rpg.inventory.SpongeInventoryService;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.inventory.runewords.RWDao;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.SpongeCharacterService;
import cz.neumimto.rpg.players.parties.PartyService;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import cz.neumimto.rpg.sponge.commands.CommandService;
import cz.neumimto.rpg.sponge.effects.SpongeEffectService;
import cz.neumimto.rpg.sponge.gui.GuiService;
import cz.neumimto.rpg.sponge.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.sponge.gui.ParticleDecorator;
import cz.neumimto.rpg.sponge.gui.VanillaMessaging;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import cz.neumimto.rpg.sponge.utils.Placeholders;

public class SpongeGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SpongeSkillService.class);
        bind(PropertyService.class).to(SpongePropertyService.class);
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
        bind(DamageService.class).to(SpongeDamageService.class);
        bind(EffectService.class).to(SpongeEffectService.class);
        bind(EntityService.class);
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
    }
}
