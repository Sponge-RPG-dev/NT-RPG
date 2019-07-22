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

package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.AbstractResourceLoader;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.commands.CommandBase;
import cz.neumimto.rpg.sponge.commands.CommandService;
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Sponge;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@SuppressWarnings("unchecked")
@Singleton
public class ResourceLoader extends AbstractResourceLoader {

    static {
        classDir = new File(NtRpgPlugin.workingDir + File.separator + "classes");
        addonDir = new File(NtRpgPlugin.workingDir + File.separator + "addons");
        addonLoadDir = new File(NtRpgPlugin.workingDir + File.separator + ".deployed");
        skilltreeDir = new File(NtRpgPlugin.workingDir + File.separator + "Skilltrees");
        localizations = new File(NtRpgPlugin.workingDir + File.separator + "localizations");
        classDir.mkdirs();
        skilltreeDir.mkdirs();
        addonDir.mkdirs();
        localizations.mkdirs();

        try {
            FileUtils.deleteDirectory(addonLoadDir);
            FileUtils.copyDirectory(addonDir, addonLoadDir, pathname -> pathname.isDirectory() || pathname.getName().endsWith(".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    private LocalizationService localizationService;

    @Inject
    private CommandService commandService;

    public void reloadLocalizations(Locale locale) {
        File localizations = new File(Rpg.get().getWorkingDirectory() + "/localizations");
        String language = locale.getLanguage();
        Log.info("Loading localization from language " + language);
        File[] files = localizations.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(language + ".properties")) {
                Log.info("Loading localization from file " + file.getName());
                try (FileInputStream input = new FileInputStream(file)) {
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
                    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                        if (entry.getValue() != null && !((String) entry.getValue()).isEmpty()) {
                            localizationService.addTranslationKey(entry.getKey().toString(), entry.getValue().toString());
                        }
                    }

                } catch (IOException e) {
                    Log.error("Could not read localization file " + file.getName(), e);
                }
            }
        }
    }

    @Override
    public Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        Object o = super.loadClass(clazz);
        if (clazz.isAnnotationPresent(ListenerClass.class)) {
            info("Registering listener class" + clazz.getName(), Rpg.get().getPluginConfig().DEBUG);
            o = injector.getInstance(clazz);
            Sponge.getGame().getEventManager().registerListeners(NtRpgPlugin.GlobalScope.plugin, o);
        }

        if (clazz.isAnnotationPresent(Command.class)) {
            o = injector.getInstance(clazz);
            info("registering command class" + clazz.getName(), Rpg.get().getPluginConfig().DEBUG);
            commandService.registerCommand((CommandBase) o);
        }
        return o;
    }

}
