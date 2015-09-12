/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto;

import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import org.slf4j.Logger;

/**
 * Created by NeumimTo on 20.7.2015.
 */
@Singleton
public class LoggingService {


    @Inject
    private Logger logger;

    public void debug(String msg) {
        if (PluginConfig.DEBUG)
            logger.info(msg);
    }
}
