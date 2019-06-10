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

package cz.neumimto.rpg.sponge.properties;

import cz.neumimto.config.blackjack.and.hookers.NotSoStupidObjectMapper;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.attributes.Attributes;
import cz.neumimto.rpg.sponge.utils.Utils;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;
import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 28.12.2014.
 */

@Singleton
public class SpongePropertyService extends PropertyServiceImpl {

    @Inject
    private NtRpgPlugin plugin;

    @Inject
    private ItemService itemService;

    @Override
    public void reLoadAttributes(Path attributeFilePath) {
        try {
            ObjectMapper<Attributes> mapper = NotSoStupidObjectMapper.forClass(Attributes.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(attributeFilePath).build();
            Attributes attributes = mapper.bind(new Attributes()).populate(hcl.load());
            attributes.getAttributes().forEach(a -> Sponge.getRegistry().register(AttributeConfig.class, new AttributeConfig(a)));


            itemService.registerItemAttributes(Rpg.get().getAttributes());
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void init(Path attributeConf, Path propertiesDump) {
        {
            StringBuilder s = new StringBuilder();
            List<String> l = new ArrayList<>(idMap.keySet());
            info(" - found " + l.size() + " Properties", pluginConfig.DEBUG);
            l.sort(Collator.getInstance());
            for (String s1 : l) {
                s.append(s1).append(Utils.LineSeparator);
            }
            try {
                Files.write(propertiesDump, s.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File f = attributeConf.toFile();
            if (!f.exists()) {
                Optional<Asset> asset = Sponge.getAssetManager().getAsset(plugin, "Attributes.conf");
                if (!asset.isPresent()) {
                    throw new IllegalStateException("Could not find an asset Attributes.conf");
                }
                try {
                    asset.get().copyToFile(f.toPath());
                } catch (IOException e) {
                    throw new IllegalStateException("Could not create Attributes.conf file", e);
                }
            }
        }
    }
}
