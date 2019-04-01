package cz.neumimto.rpg;

import cz.neumimto.core.localization.ResourceBundle;
import cz.neumimto.core.localization.ResourceBundles;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.exp.ExperienceService;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.gui.VanillaMessaging;
import cz.neumimto.rpg.inventory.CustomItemFactory;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.properties.PropertyService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.PseudoRandomDistribution;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;


/**
 * Created by NeumimTo on 19.8.2018.
 */
@Singleton
@ResourceBundles(
		@ResourceBundle("core_localization")
)
public class Init {

	@Inject	PropertyService propertyService;
	@Inject JSLoader jsLoader;
	@Inject InventoryService inventoryService;
	@Inject VanillaMessaging vanillaMessaging;
	@Inject SkillService skillService;
	@Inject EffectService effectService;
	@Inject ParticleDecorator particleDecorator;
	@Inject ExperienceService experienceService;
	@Inject CustomItemFactory customItemFactory;
	@Inject ClassService classService;
	@Inject RWService rwService;
	@Inject ResourceLoader resourceLoader;

	public void it() {
		int a = 0;
		PseudoRandomDistribution p = new PseudoRandomDistribution();
		PseudoRandomDistribution.C = new double[101];
		for (double i = 0.01; i <= 1; i += 0.01) {
			PseudoRandomDistribution.C[a] = p.c(i);
			a++;
		}
		try {
			resourceLoader.reloadLocalizations(Locale.forLanguageTag(NtRpgPlugin.pluginConfig.LOCALE));
		} catch (Exception e) {
			Log.error("Could not read localizations in locale " + NtRpgPlugin.pluginConfig.LOCALE + " - " + e.getMessage());
		}
		experienceService.load();
		inventoryService.init();
		skillService.load();
		propertyService.init();
		propertyService.reLoadAttributes();
		propertyService.loadMaximalServerPropertyValues();
		jsLoader.initEngine();

		classService.registerPlaceholders();
		rwService.load();
		classService.loadClasses();



		customItemFactory.initBuilder();
		vanillaMessaging.load();
		effectService.load();
		particleDecorator.initModels();
	}
}
