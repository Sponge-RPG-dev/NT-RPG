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

package cz.neumimto.gui;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.skills.SkillService;
import cz.neumimto.utils.FileUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by ja on 1.9.2015.
 */
@Singleton
public class GuiService {

    private Game game;

    @Inject
    private SkillService skillService;

    private Map<String, String> skillIconsUrls = new HashMap<>();

    @PostProcess(priority = 350)
    public void createStubSkillIcons() {
        Properties properties = new Properties();
        Path prop = Paths.get(NtRpgPlugin.workingDir + "/skillicons.properties");
        FileUtils.createFileIfNotExists(prop);
        try {
            properties.load(new FileInputStream(prop.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path outputdir = Paths.get(NtRpgPlugin.workingDir + "/icons/skills");
        FileUtils.createDirectoryIfNotExists(outputdir);
        try(final PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(prop.toFile(), true)))) {
            skillService.getSkills().values().stream().forEach(skill -> {
                if (!properties.containsKey(skill.getName())) {
                    BufferedImage img = createImageFromText(skill.getName());
                    File file = new File(outputdir + "/" + skill.getName() + ".png");
                    try {
                        ImageIO.write(img, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String uri = file.toPath().toUri().toString();
                    properties.put(skill.getName(), uri);
                    p.println(skill.getName()+"="+uri);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        skillIconsUrls = (Map) properties;
    }


    private BufferedImage createImageFromText(String text) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.black);
        g.drawString(text, 2, (img.getHeight() + fm.getHeight()) / 2);
        g.dispose();
        return img;
    }

    public String getIconURI(String skill) {
        return skillIconsUrls.get(skill);
    }
}
