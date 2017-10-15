package cz.neumimto;

import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

/**
 * Created by NeumimTo on 6.2.2016.
 */
@ConfigurationContainer(filename = "EffectLocalization.conf", path = "{WorkingDir}")
public class EffectLocalization {

	@ConfigValue
	public static String SOULBIND_EXPIRE = "Soulbind has expired";
}
