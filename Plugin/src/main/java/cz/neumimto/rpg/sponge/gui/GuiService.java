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

package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.skills.SkillService;
import org.spongepowered.api.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 1.9.2015.
 */
@Singleton
public class GuiService {

    @Inject
    private Game game;

    @Inject
    private SkillService skillService;

    private Map<String, String> skillIconsUrls = new HashMap<>();

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
