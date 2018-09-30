package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.exp.ExperienceService;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.gui.VanillaMessaging;
import cz.neumimto.rpg.inventory.CustomItemFactory;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.persistance.GroupDao;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.PseudoRandomDistribution;


/**
 * Created by NeumimTo on 19.8.2018.
 */
@Singleton
public class Init {

	@Inject PropertyService propertyService;
	@Inject JSLoader jsLoader;
	@Inject InventoryService inventoryService;
	@Inject VanillaMessaging vanillaMessaging;
	@Inject SkillService skillService;
	@Inject EffectService effectService;
	@Inject ParticleDecorator particleDecorator;
	@Inject ExperienceService experienceService;
	@Inject CustomItemFactory customItemFactory;
	@Inject GroupService groupService;
	@Inject GroupDao groupDao;
	@Inject RWService rwService;

	public void it() {
		int a = 0;
		PseudoRandomDistribution p = new PseudoRandomDistribution();
		PseudoRandomDistribution.C = new double[101];
		for (double i = 0.01; i <= 1; i += 0.01) {
			PseudoRandomDistribution.C[a] = p.c(i);
			a++;
		}
		experienceService.load();
		inventoryService.init();
		skillService.load();
		propertyService.init();
		jsLoader.initEngine();

		groupService.registerPlaceholders();
		rwService.load();
		groupDao.loadGuilds();
		groupDao.loadNClasses();
		groupDao.loadRaces();



		customItemFactory.initBuilder();
		vanillaMessaging.load();
		effectService.load();
		particleDecorator.initModels();
	}
}
